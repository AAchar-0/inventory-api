package io.github.aachar0.inventoryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point. Running main() starts Spring, which:
 *  1. scans this package and its sub-packages for classes marked @Component/@Service/@RestController/...
 *  2. creates ("beans") and wires them together (dependency injection)
 *  3. connects to the database, runs the Flyway migrations, and starts the web server on port 8080.
 *
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan in one annotation.
 * @ConfigurationPropertiesScan lets Spring turn the "app:" block of application.yml into AppProperties.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class InventoryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApiApplication.class, args);
    }
}
