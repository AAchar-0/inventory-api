package io.github.aachar0.inventoryapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe view of the "app:" section in application.yml.
 * A record is an immutable data class: Java generates the constructor, getters (jwt(), seed()),
 * equals, hashCode and toString for us. Spring fills it from the YAML (kebab-case "expiry-minutes" -> expiryMinutes).
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Seed seed) {

    /** Settings for signing JSON Web Tokens. */
    public record Jwt(String secret, long expiryMinutes) { }

    /** Settings for the first admin account created at startup. */
    public record Seed(String adminUsername, String adminEmail, String adminPassword) { }
}
