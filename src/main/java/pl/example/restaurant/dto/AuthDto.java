package pl.example.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.example.restaurant.entity.Role;

public class AuthDto {

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6, max = 100) String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            String phone,
            String address
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            String email,
            String firstName,
            String lastName,
            Role role
    ) {}
}

