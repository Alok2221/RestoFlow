package pl.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.restaurant.entity.Category;
import pl.example.restaurant.model.Dish;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategory(Category category);
    List<Dish> findByAvailableTrue();
    List<Dish> findByNameContainingIgnoreCase(String name);
}

