package pl.transport.truck.rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import pl.transport.truck.db.repository.UserRepository;
import pl.transport.truck.rest.jwt.JwtAuthenticationManager;
import pl.transport.truck.rest.jwt.JwtServerAuthenticationConverter;
import pl.transport.truck.rest.security.PsqlReactiveUserDetailsService;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final static String[] ALLOWED_PATHS = {"/transport-truck/user", "/transport-truck/user/login"};

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return new PsqlReactiveUserDetailsService(userRepository);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            JwtAuthenticationManager authenticationManager,
                                                            JwtServerAuthenticationConverter authenticationConverter) {

        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setServerAuthenticationConverter(authenticationConverter);
        filter.setAuthenticationFailureHandler((exchange, ex) -> Mono.fromRunnable(() -> {
            log.error("Exception: ", ex);
            exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getExchange().getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }));

        http
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, ALLOWED_PATHS).permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable();

        return http.build();
    }

}
