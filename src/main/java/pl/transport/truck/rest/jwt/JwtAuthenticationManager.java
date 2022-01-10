package pl.transport.truck.rest.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
                .flatMap(this::validate);
    }

    private Mono<UsernamePasswordAuthenticationToken> validate(TokenBearer tokenBearer) {
        return Mono.just(tokenBearer)
                .flatMap(bearer -> userDetailsService.findByUsername(jwtSupportService.getUsername(tokenBearer)))
                .filter(userDetails -> userDetails != null && jwtSupportService.isValid(tokenBearer, userDetails))
                .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token is not valid")));
    }
}
