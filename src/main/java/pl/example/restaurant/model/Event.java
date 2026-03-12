package pl.example.restaurant.model;



import jakarta.persistence.*;
import lombok.*;
import pl.example.restaurant.entity.EventType;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    private String theme;

    private String decorationPackage;

    private boolean cake;

    private String entertainment;

    private String notes;
}

