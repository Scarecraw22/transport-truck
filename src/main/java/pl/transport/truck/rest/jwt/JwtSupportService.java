package pl.transport.truck.rest.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.transport.truck.rest.config.JwtProperties;
import pl.transport.truck.utils.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class JwtSupportService {

    private final SecretKey key;
    private final JwtParser parser;

    private final Clock clock;

    @Autowired
    public JwtSupportService(Clock clock, JwtProperties jwtProperties) {
        if (StringUtils.isNullOrBlank(jwtProperties.getToken())) {
            throw new IllegalStateException("JWT token cannot be null");
        }
        this.key = Keys.hmacShaKeyFor(jwtProperties.getToken().getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();

        this.clock = clock;
    }

    public TokenBearer generate(String username) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now(clock)))
                .setExpiration(Date.from(Instant.now(clock).plus(15, ChronoUnit.MINUTES)))
                .signWith(key);

        return new TokenBearer(jwtBuilder.compact());
    }

    public String getUsername(TokenBearer tokenBearer) {
        return parser.parseClaimsJws(tokenBearer.getValue())
                .getBody()
                .getSubject();
    }

    public boolean isValid(TokenBearer tokenBearer, UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        Claims claims = parser.parseClaimsJws(tokenBearer.getValue())
                .getBody();

        boolean unexpired = claims.getExpiration().after(Date.from(Instant.now(clock)));
        boolean usernameMatches = claims.getSubject().equals(userDetails.getUsername());

        return unexpired && usernameMatches;
    }
}
