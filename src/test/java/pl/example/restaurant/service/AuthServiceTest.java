package pl.example.restaurant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.example.restaurant.dto.AuthDto;
import pl.example.restaurant.entity.Role;
import pl.example.restaurant.model.Account;
import pl.example.restaurant.repository.AccountRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AuthService authService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("password123")
                .firstName("Jan")
                .lastName("Kowalski")
                .role(Role.KLIENT)
                .registeredAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void testRegister_Success() {
        // Given
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest(
                "newuser@example.com",
                "password123",
                "Anna",
                "Nowak",
                "123456789",
                "ul. Testowa 1"
        );

        when(accountRepository.existsByEmail(request.email())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        // When
        AuthDto.AuthResponse response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("OK", response.token());
        assertEquals("newuser@example.com", response.email());
        assertEquals("Anna", response.firstName());
        assertEquals("Nowak", response.lastName());
        assertEquals(Role.KLIENT, response.role());

        verify(accountRepository).existsByEmail(request.email());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest(
                "existing@example.com",
                "password123",
                "Test",
                "User",
                null,
                null
        );

        when(accountRepository.existsByEmail(request.email())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Email already in use", exception.getMessage());
        verify(accountRepository).existsByEmail(request.email());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "test@example.com",
                "password123"
        );

        when(accountRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(testAccount));

        // When
        AuthDto.AuthResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("OK", response.token());
        assertEquals("test@example.com", response.email());
        assertEquals("Jan", response.firstName());
        assertEquals("Kowalski", response.lastName());
        assertEquals(Role.KLIENT, response.role());

        verify(accountRepository).findByEmail(request.email());
    }

    @Test
    void testLogin_InvalidEmail() {
        // Given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "nonexistent@example.com",
                "password123"
        );

        when(accountRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(accountRepository).findByEmail(request.email());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "test@example.com",
                "wrongpassword"
        );

        when(accountRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(testAccount));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(accountRepository).findByEmail(request.email());
    }
}
