package io.github.aachar0.inventoryapi.service;

import io.github.aachar0.inventoryapi.domain.Role;
import io.github.aachar0.inventoryapi.domain.User;
import io.github.aachar0.inventoryapi.dto.AuthDtos.AuthResponse;
import io.github.aachar0.inventoryapi.dto.AuthDtos.LoginRequest;
import io.github.aachar0.inventoryapi.dto.AuthDtos.RegisterRequest;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.ConflictException;
import io.github.aachar0.inventoryapi.exception.ApiExceptions.UnauthorizedException;
import io.github.aachar0.inventoryapi.repository.UserRepository;
import io.github.aachar0.inventoryapi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/** Registration and login. Business rules live in services; controllers stay thin. */
@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    /** Public registration always creates a STAFF user. Nobody can make themselves ADMIN. */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByUsername(request.username())) {
            throw new ConflictException("Username is already taken");
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmail(email)) {
            throw new ConflictException("Email is already registered");
        }
        User saved = users.save(new User(request.username(), email, encoder.encode(request.password()), Role.STAFF));
        return jwt.issue(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Same message for "unknown user" and "wrong password": attackers learn nothing about which usernames exist.
        User user = users.findByUsername(request.username())
                .filter(u -> encoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        return jwt.issue(user);
    }
}
