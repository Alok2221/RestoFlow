package pl.example.restaurant.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.restaurant.model.RestaurantTable;
import pl.example.restaurant.repository.RestaurantTableRepository;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin
public class TableController {

    private final RestaurantTableRepository tableRepository;

    public TableController(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping
    public List<RestaurantTable> all() {
        return tableRepository.findAll();
    }

    @PostMapping
    public RestaurantTable create(@Valid @RequestBody RestaurantTable table) {
        table.setId(null);
        return tableRepository.save(table);
    }

    @PutMapping("/{id}")
    public RestaurantTable update(@PathVariable("id") @NotNull Long id, @Valid @RequestBody RestaurantTable input) {
        RestaurantTable existing = tableRepository.findById(Objects.requireNonNull(id)).orElseThrow();
        existing.setTableNumber(input.getTableNumber());
        existing.setCapacity(input.getCapacity());
        existing.setLocation(input.getLocation());
        existing.setAvailable(input.isAvailable());
        existing.setActive(input.isActive());
        return tableRepository.save(existing);
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> availability(@PathVariable("id") @NotNull Long id, @RequestParam boolean available) {
        RestaurantTable existing = tableRepository.findById(Objects.requireNonNull(id)).orElseThrow();
        existing.setAvailable(available);
        tableRepository.save(existing);
        return ResponseEntity.noContent().build();
    }
}
