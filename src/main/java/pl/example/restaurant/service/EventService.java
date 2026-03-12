package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.example.restaurant.entity.EventType;
import pl.example.restaurant.model.Event;
import pl.example.restaurant.repository.EventRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private static final LocalTime OPENING_TIME = LocalTime.of(12, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(23, 0);

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event createEvent(EventType type, LocalDate date, LocalTime time,
                             String theme, String decorationPackage, boolean cake, 
                             String entertainment, String notes) {
        validateEventTime(time);
        
        Event event = Objects.requireNonNull(Event.builder()
                .eventType(type)
                .date(date)
                .time(time)
                .theme(theme)
                .decorationPackage(decorationPackage)
                .cake(cake)
                .entertainment(entertainment)
                .notes(notes)
                .build());
        return Objects.requireNonNull(eventRepository.save(event));
    }

    private void validateEventTime(LocalTime time) {
        if (time.isBefore(OPENING_TIME)) {
            throw new IllegalArgumentException("Wydarzenie może rozpocząć się najwcześniej o " + OPENING_TIME);
        }
        if (time.isAfter(CLOSING_TIME)) {
            throw new IllegalArgumentException("Wydarzenie może rozpocząć się najpóźniej o " + CLOSING_TIME);
        }
    }

    public Event getById(Long id) {
        return eventRepository.findById(Objects.requireNonNull(id)).orElseThrow();
    }

    public List<EventType> getTypes() {
        return List.of(EventType.values());
    }
}

