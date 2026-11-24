package io.github.aachar0.inventoryapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI documentation settings. After starting the app, open http://localhost:8080/swagger-ui.html
 * Click "Authorize", paste the token from /api/auth/login, and you can try every endpoint in the browser.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Inventory API",
        version = "0.1.0",
        description = "Products, categories and stock movements with JWT login."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
