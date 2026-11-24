package io.github.aachar0.inventoryapi.web;

import io.swagger.v3.oas.annotations.Hidden;      // keeps this method out of the Swagger page
import org.springframework.http.HttpStatus;       // named HTTP status codes (302 = "go to another address")
import org.springframework.http.ResponseEntity;   // lets us build a full HTTP response by hand
import org.springframework.web.bind.annotation.GetMapping;   // links a method to GET requests
import org.springframework.web.bind.annotation.RestController; // marks a class that answers web requests

import java.net.URI;                              // a typed web address

/**
 * Answers requests to the bare address ("/") by sending visitors to the Swagger page.
 * Without this, "/" is blocked by the security rules and shows a 401 error.
 */
@RestController
public class HomeController {

    @Hidden                 // do not list this helper in the API documentation
    @GetMapping("/")        // runs when someone opens the bare address
    public ResponseEntity<Void> home() {
        return ResponseEntity
                .status(HttpStatus.FOUND)                    // 302 = temporary redirect
                .location(URI.create("/swagger-ui.html"))    // where the browser should go next
                .build();                                    // no body needed, only the redirect header
    }
}