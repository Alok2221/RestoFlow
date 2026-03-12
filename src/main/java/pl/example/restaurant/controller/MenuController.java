package pl.example.restaurant.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.example.restaurant.entity.Category;
import pl.example.restaurant.model.Dish;
import pl.example.restaurant.service.MenuService;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Dish> getMenu(@RequestParam(value = "q", required = false) String query) {
        if (query != null && !query.isBlank()) {
            return menuService.searchByName(query);
        }
        return menuService.getAll();
    }

    @GetMapping(value = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Dish> getByCategory(@PathVariable Category category) {
        return menuService.getByCategory(category);
    }

    @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Dish> getAvailable() {
        return menuService.getAvailable();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Dish getById(@PathVariable Long id) {
        return menuService.getById(id);
    }
}

