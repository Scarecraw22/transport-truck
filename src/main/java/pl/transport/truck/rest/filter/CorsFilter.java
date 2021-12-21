package pl.transport.truck.rest.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.transport.truck.rest.utils.RestConsts;
import pl.transport.truck.utils.StringConsts;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CorsFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        exchange.getResponse().getHeaders()
                .add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS));
        exchange.getResponse().getHeaders()
                .add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HEADERS));

        return chain.filter(exchange);
    }
}
