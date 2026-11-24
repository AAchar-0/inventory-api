package io.github.aachar0.inventoryapi.web;

import io.github.aachar0.inventoryapi.dto.AuthDtos.AuthResponse;
import io.github.aachar0.inventoryapi.dto.AuthDtos.LoginRequest;
import io.github.aachar0.inventoryapi.dto.AuthDtos.RegisterRequest;
import io.github.aachar0.inventoryapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller maps URLs to Java methods. @RestController = every return value becomes JSON.
 * @Valid makes Spring check the validation annotations on the request record BEFORE the method runs.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @Operation(summary = "Create a STAFF account and get a token")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(request));
    }

    @Operation(summary = "Log in and get a token")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }
}
