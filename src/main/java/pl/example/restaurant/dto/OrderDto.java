package pl.example.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderDto {

    public enum OrderType {
        DOSTAWA,
        ODBIOR_OSOBISTY
    }

    public record OrderItemRequest(
            @NotNull Long dishId,
            @Min(1) int quantity,
            String notes
    ) {}

    public record CreateOrderRequest(
            @NotEmpty List<OrderItemRequest> items,
            @NotNull OrderType type,
            String deliveryAddress
    ) {}

    public record OrderSummary(
            String orderNumber,
            String status,
            String type,
            double totalAmount
    ) {}
}

