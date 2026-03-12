package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import pl.example.restaurant.entity.Category;
import pl.example.restaurant.model.Dish;
import pl.example.restaurant.repository.DishRepository;

import java.util.List;
import java.util.Objects;

@Service
public class MenuService {

    private final DishRepository dishRepository;

    public MenuService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getAll() {
        return dishRepository.findAll();
    }

    public List<Dish> getByCategory(Category category) {
        return dishRepository.findByCategory(category);
    }

    public List<Dish> getAvailable() {
        return dishRepository.findByAvailableTrue();
    }

    public Dish getById(Long id) {
        return dishRepository.findById(Objects.requireNonNull(id)).orElseThrow();
    }

    public List<Dish> searchByName(String name) {
        return dishRepository.findByNameContainingIgnoreCase(name);
    }
}

