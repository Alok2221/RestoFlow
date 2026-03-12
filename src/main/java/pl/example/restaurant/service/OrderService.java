package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.example.restaurant.dto.OrderDto;
import pl.example.restaurant.entity.OrderStatus;
import pl.example.restaurant.model.Dish;
import pl.example.restaurant.model.Order;
import pl.example.restaurant.model.OrderItem;
import pl.example.restaurant.repository.DishRepository;
import pl.example.restaurant.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;

    public OrderService(OrderRepository orderRepository, DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
    }

    @Transactional
    public Order createOrder(OrderDto.CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setClient(null); // od teraz nie ma przypisanego kienta
        order.setCreatedAt(OffsetDateTime.now());
        order.setStatus(OrderStatus.OCZEKUJACE);
        order.setType(Order.Type.valueOf(request.type().name()));
        order.setDeliveryAddress(request.deliveryAddress());

        final BigDecimal[] total = {BigDecimal.ZERO};

        List<OrderItem> items = request.items().stream().map(itemReq -> {
            Dish dish = dishRepository.findById(Objects.requireNonNull(itemReq.dishId())).orElseThrow();
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setDish(dish);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(dish.getPrice());
            item.setNotes(itemReq.notes());
            total[0] = total[0].add(dish.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
            return item;
        }).toList();

        order.setItems(items);
        order.setTotalAmount(total[0]);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllWithItems();
    }

    public Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumberWithItems(orderNumber).orElseThrow();
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(Objects.requireNonNull(id)).orElseThrow();
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        return orderRepository.findByOrderNumberWithItems(saved.getOrderNumber()).orElseThrow();
    }
}
