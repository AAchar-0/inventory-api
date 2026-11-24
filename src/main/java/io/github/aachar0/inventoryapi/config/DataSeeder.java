package io.github.aachar0.inventoryapi.config;

import io.github.aachar0.inventoryapi.domain.Role;
import io.github.aachar0.inventoryapi.domain.User;
import io.github.aachar0.inventoryapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * Runs once at startup (ApplicationRunner) and creates the first ADMIN account if it is missing.
 * Public registration only creates STAFF users, so without this nobody could create products.
 *
 * Security: no password is written in the source code. It comes from APP_ADMIN_PASSWORD, or is
 * generated randomly and printed once in the log (for local development only).
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AppProperties props;

    // Constructor injection: Spring passes in the beans we need. This is the preferred style.
    public DataSeeder(UserRepository users, PasswordEncoder encoder, AppProperties props) {
        this.users = users;
        this.encoder = encoder;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        AppProperties.Seed seed = props.seed();
        if (users.existsByUsername(seed.adminUsername())) {
            return; // already there: nothing to do
        }
        String password = seed.adminPassword();
        boolean generated = password == null || password.isBlank();
        if (generated) {
            password = randomPassword(20);
        }
        users.save(new User(seed.adminUsername(), seed.adminEmail(), encoder.encode(password), Role.ADMIN));

        if (generated) {
            log.warn("APP_ADMIN_PASSWORD not set. Generated admin password (shown once, local use only): {}", password);
        } else {
            log.info("Admin user '{}' created from APP_ADMIN_PASSWORD.", seed.adminUsername());
        }
    }

    private static String randomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
