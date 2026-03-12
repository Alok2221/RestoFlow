package pl.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.restaurant.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}

