package pl.example.restaurant.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.restaurant.dto.AuthDto;
import pl.example.restaurant.repository.AccountRepository;
import pl.example.restaurant.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final AccountRepository accountRepository;

    public AuthController(AuthService authService, AccountRepository accountRepository) {
        this.authService = authService;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    public record ProfileResponse(Long id, String email, String firstName, String lastName,
                                  String role, String phone, String address) {}

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.ok().build();
        }
        return accountRepository.findByEmail(email)
                .map(account -> ResponseEntity.ok(new ProfileResponse(
                        account.getId(),
                        account.getEmail(),
                        account.getFirstName(),
                        account.getLastName(),
                        account.getRole().name(),
                        account.getPhone(),
                        account.getAddress()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
