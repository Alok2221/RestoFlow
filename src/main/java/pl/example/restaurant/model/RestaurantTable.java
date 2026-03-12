package pl.example.restaurant.model;



import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {

    public enum Location {
        TARAS,
        SALA_GLOWNA,
        SALON_VIP,
        BAR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Location location;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private boolean active;
}

