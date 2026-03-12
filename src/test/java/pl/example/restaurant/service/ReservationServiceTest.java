package pl.example.restaurant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.example.restaurant.dto.ReservationDto;
import pl.example.restaurant.model.Reservation;
import pl.example.restaurant.model.ReservationTable;
import pl.example.restaurant.model.RestaurantTable;
import pl.example.restaurant.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TableAssignmentService tableAssignmentService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private RestaurantTable testTable;

    @BeforeEach
    void setUp() {
        testTable = RestaurantTable.builder()
                .id(1L)
                .tableNumber("1")
                .capacity(4)
                .location(RestaurantTable.Location.SALA_GLOWNA)
                .available(true)
                .active(true)
                .build();

        ReservationTable reservationTable = ReservationTable.builder()
                .id(1L)
                .reservation(null)
                .table(testTable)
                .build();

        testReservation = Reservation.builder()
                .id(1L)
                .reservationNumber("RES-12345678")
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(20, 0))
                .peopleCount(4)
                .status(Reservation.Status.POTWIERDZONA)
                .tables(Arrays.asList(reservationTable))
                .build();
    }

    @Test
    void testCreateReservation_Success() {
        // Given
        ReservationDto.CreateReservationRequest request = new ReservationDto.CreateReservationRequest(
                LocalDate.now().plusDays(1),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                4,
                "Test notes"
        );

        ReservationTable assignedTable = ReservationTable.builder()
                .table(testTable)
                .build();

        when(tableAssignmentService.assignTables(any(Reservation.class)))
                .thenReturn(Arrays.asList(assignedTable));
        Reservation[] savedRef = new Reservation[1];
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            savedRef[0] = r;
            return r;
        });
        when(reservationRepository.findByReservationNumberWithTables(anyString()))
                .thenAnswer(inv -> Optional.of(savedRef[0]));

        // When
        Reservation result = reservationService.createReservation(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getReservationNumber());
        assertTrue(result.getReservationNumber().startsWith("RES-"));
        assertEquals(request.date(), result.getDate());
        assertEquals(request.startTime(), result.getStartTime());
        assertEquals(request.endTime(), result.getEndTime());
        assertEquals(request.peopleCount(), result.getPeopleCount());
        assertEquals(Reservation.Status.POTWIERDZONA, result.getStatus());
        assertEquals("Test notes", result.getNotes());
        assertFalse(result.getTables().isEmpty());

        verify(tableAssignmentService).assignTables(any(Reservation.class));
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void testCreateReservation_NoTablesAvailable() {
        // Given
        ReservationDto.CreateReservationRequest request = new ReservationDto.CreateReservationRequest(
                LocalDate.now().plusDays(1),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                10,
                null
        );

        when(tableAssignmentService.assignTables(any(Reservation.class)))
                .thenReturn(List.of());

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationService.createReservation(request)
        );

        assertEquals("Brak dostępnych stolików dla podanych parametrów", exception.getMessage());
        verify(tableAssignmentService).assignTables(any(Reservation.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testGetByDate() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByDateWithTables(date)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getByDate(date);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository).findByDateWithTables(date);
    }

    @Test
    void testGetByNumber_Success() {
        // Given
        when(reservationRepository.findByReservationNumberWithTables("RES-12345678"))
                .thenReturn(Optional.of(testReservation));

        // When
        Reservation result = reservationService.getByNumber("RES-12345678");

        // Then
        assertNotNull(result);
        assertEquals("RES-12345678", result.getReservationNumber());
        verify(reservationRepository).findByReservationNumberWithTables("RES-12345678");
    }

    @Test
    void testGetByNumber_NotFound() {
        // Given
        when(reservationRepository.findByReservationNumberWithTables("RES-NOTFOUND"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> reservationService.getByNumber("RES-NOTFOUND"));
        verify(reservationRepository).findByReservationNumberWithTables("RES-NOTFOUND");
    }

    @Test
    void testCancel() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        reservationService.cancel(1L);

        // Then
        assertEquals(Reservation.Status.ANULOWANA, testReservation.getStatus());
        verify(reservationRepository).findById(1L);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void testCancel_NotFound() {
        // Given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> reservationService.cancel(999L));
        verify(reservationRepository).findById(999L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testHasAvailability_Available() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(18, 0);
        LocalTime end = LocalTime.of(20, 0);
        int peopleCount = 4;

        ReservationTable assignedTable = ReservationTable.builder()
                .table(testTable)
                .build();

        when(tableAssignmentService.assignTables(any(Reservation.class)))
                .thenReturn(Collections.singletonList(assignedTable));

        // When
        boolean result = reservationService.hasAvailability(date, start, end, peopleCount);

        // Then
        assertTrue(result);
        verify(tableAssignmentService).assignTables(any(Reservation.class));
    }

    @Test
    void testHasAvailability_NotAvailable() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime start = LocalTime.of(18, 0);
        LocalTime end = LocalTime.of(20, 0);
        int peopleCount = 20;

        when(tableAssignmentService.assignTables(any(Reservation.class)))
                .thenReturn(List.of());

        // When
        boolean result = reservationService.hasAvailability(date, start, end, peopleCount);

        // Then
        assertFalse(result);
        verify(tableAssignmentService).assignTables(any(Reservation.class));
    }
}
