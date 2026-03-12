package pl.example.restaurant.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.restaurant.dto.ReservationDto;
import pl.example.restaurant.model.Reservation;
import pl.example.restaurant.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody ReservationDto.CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @GetMapping("/available")
    public ResponseEntity<Boolean> available(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam int peopleCount
    ) {
        boolean ok = reservationService.hasAvailability(date, startTime, endTime, peopleCount);
        return ResponseEntity.ok(ok);
    }

    @GetMapping("/{reservationNumber}")
    public Reservation getByNumber(@PathVariable String reservationNumber) {
        return reservationService.getByNumber(reservationNumber);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/day/{date}")
    public List<Reservation> byDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reservationService.getByDate(date);
    }
}
