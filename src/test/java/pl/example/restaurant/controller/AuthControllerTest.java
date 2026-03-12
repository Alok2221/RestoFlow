package pl.example.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.example.restaurant.dto.AuthDto;
import pl.example.restaurant.entity.Role;
import pl.example.restaurant.model.Account;
import pl.example.restaurant.repository.AccountRepository;
import pl.example.restaurant.service.AuthService;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private AccountRepository accountRepository;

    @Test
    void testRegister_Success() throws Exception {
        // Given
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest(
                "test@example.com",
                "password123",
                "Jan",
                "Kowalski",
                "123456789",
                "ul. Testowa 1"
        );

        AuthDto.AuthResponse response = new AuthDto.AuthResponse(
                "OK",
                "test@example.com",
                "Jan",
                "Kowalski",
                Role.KLIENT
        );

        when(authService.register(any(AuthDto.RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("OK"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.role").value("KLIENT"));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(
                "test@example.com",
                "password123"
        );

        AuthDto.AuthResponse response = new AuthDto.AuthResponse(
                "OK",
                "test@example.com",
                "Jan",
                "Kowalski",
                Role.KLIENT
        );

        when(authService.login(any(AuthDto.LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("OK"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }

    @Test
    void testGetMe_Success() throws Exception {
        // Given
        Account account = Account.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .role(Role.KLIENT)
                .phone("123456789")
                .address("ul. Testowa 1")
                .registeredAt(OffsetDateTime.now())
                .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(account));

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.role").value("KLIENT"));
    }

    @Test
    void testGetMe_NoEmail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }
}
