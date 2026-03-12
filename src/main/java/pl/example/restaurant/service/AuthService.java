package pl.example.restaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.example.restaurant.dto.AuthDto;
import pl.example.restaurant.model.Account;
import pl.example.restaurant.entity.Role;
import pl.example.restaurant.repository.AccountRepository;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class AuthService {

    private final AccountRepository accountRepository;

    public AuthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Account account = Objects.requireNonNull(Account.builder()
                .email(request.email())
                .passwordHash(request.password())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .address(request.address())
                .role(Role.KLIENT)
                .registeredAt(OffsetDateTime.now())
                .build());

        Account savedAccount = Objects.requireNonNull(accountRepository.save(account));
        return new AuthDto.AuthResponse(
                "OK", savedAccount.getEmail(), savedAccount.getFirstName(), savedAccount.getLastName(), 
                Objects.requireNonNull(savedAccount.getRole())
        );
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .filter(a -> a.getPasswordHash().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        return new AuthDto.AuthResponse(
                "OK", account.getEmail(), account.getFirstName(), account.getLastName(), account.getRole()
        );
    }
}
