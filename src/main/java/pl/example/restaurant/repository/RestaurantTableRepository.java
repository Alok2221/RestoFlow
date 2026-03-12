package pl.example.restaurant.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.restaurant.model.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
}

