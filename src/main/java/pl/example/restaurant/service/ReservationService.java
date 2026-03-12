package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.example.restaurant.dto.ReservationDto;
import pl.example.restaurant.model.Reservation;
import pl.example.restaurant.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReservationService {

    private static final LocalTime OPENING_TIME = LocalTime.of(12, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(23, 0);
    private static final int MIN_RESERVATION_DURATION_MINUTES = 60;
    private static final int MAX_RESERVATION_DURATION_HOURS = 4;

    private final ReservationRepository reservationRepository;
    private final TableAssignmentService tableAssignmentService;

    public ReservationService(ReservationRepository reservationRepository,
                              TableAssignmentService tableAssignmentService) {
        this.reservationRepository = reservationRepository;
        this.tableAssignmentService = tableAssignmentService;
    }

    @Transactional
    public Reservation createReservation(ReservationDto.CreateReservationRequest request) {
        validateReservationTime(request.startTime(), request.endTime());
        
        Reservation reservation = new Reservation();
        reservation.setReservationNumber("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setClient(null); // od teraz nie ma przypisanego kienta
        reservation.setDate(request.date());
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setPeopleCount(request.peopleCount());
        reservation.setStatus(Reservation.Status.POTWIERDZONA);
        reservation.setNotes(request.notes());

        var assigned = tableAssignmentService.assignTables(reservation);
        if (assigned.isEmpty()) {
            throw new IllegalStateException("Brak dostępnych stolików dla podanych parametrów");
        }

        reservation.setTables(assigned);
        Reservation saved = reservationRepository.save(reservation);
        return reservationRepository.findByReservationNumberWithTables(saved.getReservationNumber()).orElseThrow();
    }

    private void validateReservationTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(OPENING_TIME)) {
            throw new IllegalArgumentException("Rezerwacja może rozpocząć się najwcześniej o " + OPENING_TIME);
        }
        if (endTime.isAfter(CLOSING_TIME)) {
            throw new IllegalArgumentException("Rezerwacja musi zakończyć się najpóźniej o " + CLOSING_TIME);
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia");
        }
        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < MIN_RESERVATION_DURATION_MINUTES) {
            throw new IllegalArgumentException("Minimalny czas rezerwacji to " + MIN_RESERVATION_DURATION_MINUTES + " minut");
        }
        if (durationMinutes > MAX_RESERVATION_DURATION_HOURS * 60) {
            throw new IllegalArgumentException("Maksymalny czas rezerwacji to " + MAX_RESERVATION_DURATION_HOURS + " godziny");
        }
    }

    public List<Reservation> getByDate(LocalDate date) {
        return reservationRepository.findByDateWithTables(date);
    }

    public Reservation getByNumber(String reservationNumber) {
        return reservationRepository.findByReservationNumberWithTables(reservationNumber).orElseThrow();
    }

    @Transactional
    public void cancel(Long id) {
        Reservation reservation = reservationRepository.findById(Objects.requireNonNull(id)).orElseThrow();
        reservation.setStatus(Reservation.Status.ANULOWANA);
        reservationRepository.save(reservation);
    }

    public boolean hasAvailability(LocalDate date, LocalTime start, LocalTime end, int peopleCount) {
        try {
            validateReservationTime(start, end);
        } catch (IllegalArgumentException e) {
            return false;
        }
        Reservation tmp = new Reservation();
        tmp.setDate(date);
        tmp.setStartTime(start);
        tmp.setEndTime(end);
        tmp.setPeopleCount(peopleCount);
        return !tableAssignmentService.assignTables(tmp).isEmpty();
    }
}
