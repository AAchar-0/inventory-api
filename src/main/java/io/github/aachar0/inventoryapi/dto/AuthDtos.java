package io.github.aachar0.inventoryapi.dto;

import io.github.aachar0.inventoryapi.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTOs (Data Transfer Objects) are the shapes of JSON going in and out of the API.
 * We never expose entities directly: a DTO lets us hide fields (like password_hash) and change the
 * database without breaking clients. Records + validation annotations = short and safe.
 */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50)
            @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "only letters, digits, dot, dash and underscore")
            String username,

            @NotBlank @Email @Size(max = 120)
            String email,

            // BCrypt only uses the first 72 bytes of a password, so we cap the length at 72.
            @NotBlank @Size(min = 10, max = 72, message = "must be 10 to 72 characters")
            String password) {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record AuthResponse(String token, String tokenType, long expiresInSeconds, String username, Role role) {
    }
}
