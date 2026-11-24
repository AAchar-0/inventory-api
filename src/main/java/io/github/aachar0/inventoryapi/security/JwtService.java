package io.github.aachar0.inventoryapi.security;

import io.github.aachar0.inventoryapi.config.AppProperties;
import io.github.aachar0.inventoryapi.domain.User;
import io.github.aachar0.inventoryapi.dto.AuthDtos.AuthResponse;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Creates signed tokens. A JWT has three Base64 parts: header.payload.signature.
 * The payload ("claims") says who the user is and when the token expires; the signature proves
 * nobody changed it. Never put secrets (like passwords) in the claims: anyone can read them.
 */
@Service
public class JwtService {

    private static final String ISSUER = "inventory-api";

    private final JwtEncoder encoder;
    private final AppProperties props;

    public JwtService(JwtEncoder encoder, AppProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public AuthResponse issue(User user) {
        Duration lifetime = Duration.ofMinutes(props.jwt().expiryMinutes());
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .subject(user.getUsername())
                .claim("roles", List.of(user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AuthResponse(token, "Bearer", lifetime.toSeconds(), user.getUsername(), user.getRole());
    }
}
