package tasksplanner.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import tasksplanner.response.UserResponse;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final Duration EXPIRED_IN = Duration.ofHours(2); //.ofMinutes(1);

    private final JwtEncoder jwtEncoder;

    public Jwt generateToken(UserResponse user) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.id().toString())
                .claim("email", user.email())
                .issuedAt(now)
                .expiresAt(now.plus(EXPIRED_IN))
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(header, claims);

        Jwt jwt = jwtEncoder.encode(parameters);

        log.debug(
                "JWT generated for userId={}, expiresAt={}",
                user.id(),
                jwt.getExpiresAt()
        );

        return jwt;
    }
}
