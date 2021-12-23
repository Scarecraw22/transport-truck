package pl.transport.truck.rest.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pl.transport.truck.rest.interceptor.RequestLoggingInterceptor;
import pl.transport.truck.rest.interceptor.ResponseLoggingInterceptor;
import pl.transport.truck.rest.utils.CustomHeaders;
import pl.transport.truck.utils.CollectionUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class RequestLoggingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        log.info("Request headers: {}", httpHeaders);
        log.info("Request cookies: {}", exchange.getRequest().getCookies().toSingleValueMap());

        String requestId;
        try {
            requestId = getRequestId(httpHeaders);
        } catch (IllegalStateException e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(e.getMessage().getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

        final long startTime = System.currentTimeMillis();
        MDC.put(CustomHeaders.REQUEST_ID.getValue(), requestId);
        ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {

            @Override
            public ServerHttpRequest getRequest() {
                return new RequestLoggingInterceptor(super.getRequest());
            }

            @Override
            public ServerHttpResponse getResponse() {
                return new ResponseLoggingInterceptor(super.getResponse(), startTime, requestId);
            }
        };
        return chain.filter(exchangeDecorator)

                .doOnSuccess(aVoid -> {
                    logResponse(startTime, exchange.getResponse(), exchange.getResponse().getStatusCode());
                })
                .doOnError(aVoid -> {
                    logResponse(startTime, exchange.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .doFinally(signalType -> MDC.clear());
    }

    private void logResponse(long startTime, ServerHttpResponse response, HttpStatus overriddenStatus) {
        final long duration = System.currentTimeMillis() - startTime;
        log.info("Response[{}ms]: status={}, headers={}", duration, overriddenStatus, response.getHeaders());
    }

    private String getRequestId(HttpHeaders httpHeaders) {
        List<String> requestIdHeaders = httpHeaders.get(CustomHeaders.REQUEST_ID.getValue());

        if (CollectionUtils.isNotEmpty(requestIdHeaders)) {
            return requestIdHeaders.get(0);
        } else {
            log.info("{} header us required", CustomHeaders.REQUEST_ID.getValue());
            throw new IllegalStateException(CustomHeaders.REQUEST_ID.getValue() + " header is required");
        }
    }

    private static void logWithContext(HttpHeaders headers, Consumer<HttpHeaders> logAction) {
        try {
            headers.forEach((name, values) -> MDC.put(name, values.get(0)));
            logAction.accept(headers);
        } finally {
            headers.keySet().forEach(MDC::remove);
        }
    }
}
