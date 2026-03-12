package pl.example.restaurant.dto;



import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationDto {

    public record CreateReservationRequest(
            @NotNull @Future LocalDate date,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime,
            @Min(1) int peopleCount,
            String notes
    ) {}

    public record AssignedTablesResponse(
            String reservationNumber,
            List<Long> tableIds
    ) {}

    public record AvailabilityRequest(
            @NotNull LocalDate date,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime,
            @Min(1) int peopleCount
    ) {}
}

