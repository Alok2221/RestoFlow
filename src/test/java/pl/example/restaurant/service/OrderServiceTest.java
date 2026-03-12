package pl.example.restaurant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.example.restaurant.dto.OrderDto;
import pl.example.restaurant.entity.OrderStatus;
import pl.example.restaurant.model.Dish;
import pl.example.restaurant.model.Order;
import pl.example.restaurant.repository.DishRepository;
import pl.example.restaurant.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private OrderService orderService;

    private Dish testDish1;
    private Dish testDish2;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testDish1 = Dish.builder()
                .id(1L)
                .name("Zupa pomidorowa")
                .price(new BigDecimal("18.50"))
                .available(true)
                .build();

        testDish2 = Dish.builder()
                .id(2L)
                .name("Schabowy")
                .price(new BigDecimal("45.00"))
                .available(true)
                .build();

        testOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .status(OrderStatus.OCZEKUJACE)
                .totalAmount(new BigDecimal("63.50"))
                .build();
    }

    @Test
    void testCreateOrder_Success() {
        // Given
        OrderDto.CreateOrderRequest request = new OrderDto.CreateOrderRequest(
                Arrays.asList(
                        new OrderDto.OrderItemRequest(1L, 2, ""),
                        new OrderDto.OrderItemRequest(2L, 1, "")
                ),
                OrderDto.OrderType.DOSTAWA,
                "ul. Testowa 1"
        );

        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish1));
        when(dishRepository.findById(2L)).thenReturn(Optional.of(testDish2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = orderService.createOrder(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getOrderNumber());
        assertTrue(result.getOrderNumber().startsWith("ORD-"));
        assertEquals(OrderStatus.OCZEKUJACE, result.getStatus());
        assertEquals(Order.Type.DOSTAWA, result.getType());
        assertEquals("ul. Testowa 1", result.getDeliveryAddress());
        assertNotNull(result.getTotalAmount());
        assertEquals(2, result.getItems().size());

        // Sprawdź kalkulację: 18.50 * 2 + 45.00 * 1 = 82.00
        BigDecimal expectedTotal = new BigDecimal("18.50").multiply(new BigDecimal("2"))
                .add(new BigDecimal("45.00"));
        assertEquals(0, result.getTotalAmount().compareTo(expectedTotal));

        verify(dishRepository, times(2)).findById(any());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_Pickup() {
        // Given
        OrderDto.CreateOrderRequest request = new OrderDto.CreateOrderRequest(
                Arrays.asList(new OrderDto.OrderItemRequest(1L, 1, "")),
                OrderDto.OrderType.ODBIOR_OSOBISTY,
                null
        );

        when(dishRepository.findById(1L)).thenReturn(Optional.of(testDish1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = orderService.createOrder(request);

        // Then
        assertNotNull(result);
        assertEquals(Order.Type.ODBIOR_OSOBISTY, result.getType());
        assertNull(result.getDeliveryAddress());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testGetAllOrders() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAllWithItems()).thenReturn(orders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAllWithItems();
    }

    @Test
    void testGetByOrderNumber_Success() {
        // Given
        when(orderRepository.findByOrderNumberWithItems("ORD-12345678")).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.getByOrderNumber("ORD-12345678");

        // Then
        assertNotNull(result);
        assertEquals("ORD-12345678", result.getOrderNumber());
        verify(orderRepository).findByOrderNumberWithItems("ORD-12345678");
    }

    @Test
    void testGetByOrderNumber_NotFound() {
        // Given
        when(orderRepository.findByOrderNumberWithItems("ORD-NOTFOUND")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> orderService.getByOrderNumber("ORD-NOTFOUND"));
        verify(orderRepository).findByOrderNumberWithItems("ORD-NOTFOUND");
    }

    @Test
    void testUpdateStatus() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderRepository.findByOrderNumberWithItems("ORD-12345678")).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.updateStatus(1L, OrderStatus.W_PRZYGOTOWANIU);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.W_PRZYGOTOWANIU, result.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderRepository).findByOrderNumberWithItems("ORD-12345678");
    }

    @Test
    void testUpdateStatus_OrderNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> orderService.updateStatus(999L, OrderStatus.GOTOWE));
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
