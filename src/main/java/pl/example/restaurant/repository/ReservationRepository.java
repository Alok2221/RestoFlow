package pl.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.example.restaurant.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNumber(String reservationNumber);

    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.tables rt " +
           "LEFT JOIN FETCH rt.table " +
           "LEFT JOIN FETCH r.client " +
           "WHERE r.reservationNumber = :number")
    Optional<Reservation> findByReservationNumberWithTables(@Param("number") String reservationNumber);

    List<Reservation> findByDate(LocalDate date);

    @Query("SELECT DISTINCT r FROM Reservation r " +
           "LEFT JOIN FETCH r.tables rt " +
           "LEFT JOIN FETCH rt.table " +
           "LEFT JOIN FETCH r.client " +
           "WHERE r.date = :date")
    List<Reservation> findByDateWithTables(@Param("date") LocalDate date);

    List<Reservation> findByDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDate date, LocalTime end, LocalTime start);

    @Query("SELECT DISTINCT r FROM Reservation r " +
           "LEFT JOIN FETCH r.tables rt " +
           "LEFT JOIN FETCH rt.table " +
           "WHERE r.date = :date AND r.startTime <= :end AND r.endTime >= :start")
    List<Reservation> findByDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualWithTables(
            @Param("date") LocalDate date, @Param("end") LocalTime end, @Param("start") LocalTime start);
}

