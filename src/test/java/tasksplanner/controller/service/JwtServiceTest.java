package tasksplanner.controller.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import tasksplanner.response.UserResponse;
import tasksplanner.security.JwtService;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @InjectMocks
    private JwtService service;

    @Mock
    private JwtEncoder encoder;

    @Captor
    private ArgumentCaptor<JwtEncoderParameters> params;

    @Test
    public void generateTokenIsSucceeded() {
        Long userId = 1L;
        String email = "tim11@mail.ru";

        UserResponse user = new UserResponse(1L, email);

        Instant issued = Instant.now();
        Instant expiredIn = issued.plus(Duration.ofHours(2));

        Jwt expected = new Jwt(
                "mocked-tkn",
                issued,
                expiredIn,
                Map.of("alg", "HS256"),
                Map.of("sub", user.id(),
                        "email", email
                )
        );

        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(expected);

        Jwt actual = service.generateToken(user);

        verify(encoder, times(1)).encode(params.capture());

        JwtEncoderParameters captorValue = params.getValue();

        JwtClaimsSet claims = captorValue.getClaims();

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.getClaimAsString("email")).isEqualTo(email);

        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiresAt()).isNotNull();
        assertThat(Duration.between(claims.getIssuedAt(), claims.getExpiresAt())).isEqualTo(Duration.ofHours(2));

        assertThat(actual).isEqualTo(expected);
    }
}
