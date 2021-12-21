package pl.transport.truck.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import pl.transport.truck.db.repository.UserRepository;
import pl.transport.truck.rest.security.PsqlReactiveUserDetailsService;
import pl.transport.truck.rest.utils.RestConsts;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final static String[] ALLOWED_PATHS = {"/transport-truck/customer", "/transport-truck/customer/login"};


    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        return new PsqlReactiveUserDetailsService(userRepository);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().csrfTokenRepository(new WebSessionServerCsrfTokenRepository())
                .requireCsrfProtectionMatcher(getURLsForDisabledCSRF())
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, ALLOWED_PATHS).permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable();

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowedMethods(RestConsts.ALLOWED_HTTP_METHODS);
        config.setAllowedHeaders(RestConsts.ALLOWED_HEADERS);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    private NegatedServerWebExchangeMatcher getURLsForDisabledCSRF() {
        return new NegatedServerWebExchangeMatcher(exchange -> ServerWebExchangeMatchers.pathMatchers(ALLOWED_PATHS).matches(exchange));
    }
}
