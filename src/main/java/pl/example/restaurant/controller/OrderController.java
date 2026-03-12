package pl.example.restaurant.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.restaurant.dto.OrderDto;
import pl.example.restaurant.entity.OrderStatus;
import pl.example.restaurant.model.Order;
import pl.example.restaurant.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto.CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping
    public List<Order> allOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderNumber}")
    public Order getByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getByOrderNumber(orderNumber);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}
