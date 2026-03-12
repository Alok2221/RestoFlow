package pl.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.example.restaurant.model.Account;
import pl.example.restaurant.model.Order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.dish " +
           "LEFT JOIN FETCH o.client")
    List<Order> findAllWithItems();

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.dish " +
           "LEFT JOIN FETCH o.client " +
           "WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);

    List<Order> findByClient(Account client);
    List<Order> findByCreatedAtBetween(OffsetDateTime from, OffsetDateTime to);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.dish " +
           "LEFT JOIN FETCH o.client " +
           "WHERE o.createdAt BETWEEN :from AND :to")
    List<Order> findByCreatedAtBetweenWithItems(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}

