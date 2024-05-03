package ru.ifmo.puls.auth.repo;

import java.util.Optional;

import ru.ifmo.puls.domain.User;

public interface UserRepository { //extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    User save(User user);

    Optional<User> findById(long id);
}
