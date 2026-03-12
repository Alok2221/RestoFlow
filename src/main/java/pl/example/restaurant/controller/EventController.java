package pl.example.restaurant.controller;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.restaurant.entity.EventType;
import pl.example.restaurant.model.Event;
import pl.example.restaurant.service.EventService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    public record CreateEventRequest(
            @NotNull EventType type,
            @NotNull @Future LocalDate date,
            @NotNull LocalTime time,
            String theme,
            String decorationPackage,
            boolean cake,
            String entertainment,
            String notes
    ) {}

    @PostMapping
    public ResponseEntity<Event> create(@jakarta.validation.Valid @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(
                request.type(),
                request.date(),
                request.time(),
                request.theme(),
                request.decorationPackage(),
                request.cake(),
                request.entertainment(),
                request.notes()
        );
        return ResponseEntity.ok(event);
    }

    @GetMapping("/types")
    public List<EventType> types() {
        return eventService.getTypes();
    }

    @GetMapping("/{id}")
    public Event get(@PathVariable Long id) {
        return eventService.getById(id);
    }
}

