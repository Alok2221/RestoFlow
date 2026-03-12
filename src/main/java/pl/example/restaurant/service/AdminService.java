package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import pl.example.restaurant.model.Order;
import pl.example.restaurant.model.Reservation;
import pl.example.restaurant.repository.OrderRepository;
import pl.example.restaurant.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    public AdminService(OrderRepository orderRepository, ReservationRepository reservationRepository) {
        this.orderRepository = orderRepository;
        this.reservationRepository = reservationRepository;
    }

    public record Statistics(
            long ordersCount,
            double totalSales,
            long reservationsCount
    ) {}

    public Statistics statisticsToday() {
        LocalDate today = LocalDate.now();
        OffsetDateTime from = today.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime to = today.atTime(LocalTime.MAX).atOffset(OffsetDateTime.now().getOffset());

        List<Order> orders = orderRepository.findByCreatedAtBetween(from, to);
        BigDecimal total = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long reservations = reservationRepository.findByDate(today).size();
        return new Statistics(orders.size(), total.doubleValue(), reservations);
    }

    public List<Order> ordersToday() {
        LocalDate today = LocalDate.now();
        OffsetDateTime from = today.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime to = today.atTime(LocalTime.MAX).atOffset(OffsetDateTime.now().getOffset());
        return orderRepository.findByCreatedAtBetweenWithItems(from, to);
    }

    public List<Reservation> reservationsToday() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findByDateWithTables(today);
    }
}

