package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import pl.example.restaurant.model.Reservation;
import pl.example.restaurant.model.ReservationTable;
import pl.example.restaurant.model.RestaurantTable;
import pl.example.restaurant.repository.ReservationRepository;
import pl.example.restaurant.repository.RestaurantTableRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableAssignmentService {

    private static final int BUFFER_MINUTES = 30;

    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public TableAssignmentService(RestaurantTableRepository tableRepository,
                                  ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationTable> assignTables(Reservation reservation) {
        LocalDate date = reservation.getDate();
        LocalTime start = reservation.getStartTime().minusMinutes(BUFFER_MINUTES);
        LocalTime end = reservation.getEndTime().plusMinutes(BUFFER_MINUTES);

        List<Reservation> overlapping = reservationRepository
                .findByDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualWithTables(date, end, start);

        List<Long> occupiedTableIds = overlapping.stream()
                .flatMap(r -> r.getTables().stream())
                .map(rt -> rt.getTable().getId())
                .collect(Collectors.toSet())
                .stream().toList();

        List<RestaurantTable> candidates = tableRepository.findAll().stream()
                .filter(RestaurantTable::isActive)
                .filter(RestaurantTable::isAvailable)
                .filter(t -> !occupiedTableIds.contains(t.getId()))
                .sorted(Comparator.comparingInt(RestaurantTable::getCapacity))
                .toList();

        List<ReservationTable> assigned = new ArrayList<>();
        int remaining = reservation.getPeopleCount();

        for (RestaurantTable table : candidates) {
            if (remaining <= 0) break;
            assigned.add(ReservationTable.builder()
                    .reservation(reservation)
                    .table(table)
                    .build());
            remaining -= table.getCapacity();
        }

        if (remaining > 0) {
            return List.of();
        }

        reservation.setTables(assigned);
        return assigned;
    }
}

