package ru.ifmo.puls.auth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ifmo.puls.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
