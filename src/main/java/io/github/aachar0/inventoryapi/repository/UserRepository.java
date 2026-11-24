package io.github.aachar0.inventoryapi.repository;

import io.github.aachar0.inventoryapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * A repository is an interface only: Spring Data writes the implementation at startup.
 * JpaRepository<User, Long> already gives save, findById, findAll, deleteById, count, ...
 * Method names below are parsed into SQL: "findByUsername" -> SELECT ... WHERE username = ?
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
