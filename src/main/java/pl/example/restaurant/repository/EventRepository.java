package pl.example.restaurant.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.restaurant.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}

