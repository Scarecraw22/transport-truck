package pl.transport.truck.rest.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.transport.truck.rest.jwt.ex.InvalidBearerTokenException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtSupportService jwtSupportService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth instanceof TokenBearer)
                .cast(TokenBearer.class)
                .map(this::validate)
                .onErrorMap(error -> new InvalidBearerTokenException(error.getMessage()));
    }

    private Authentication validate(TokenBearer tokenBearer) {
        String username = jwtSupportService.getUsername(tokenBearer);
        UserDetails userDetails = userDetailsService.findByUsername(username).single().block(Duration.ofSeconds(10));

        if (userDetails != null && jwtSupportService.isValid(tokenBearer, userDetails)) {
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        } else {
            throw new IllegalArgumentException("Token is not valid");
        }
    }
}
