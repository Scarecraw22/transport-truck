package pl.transport.truck.rest.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import pl.transport.truck.rest.utils.CustomHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

@Slf4j
public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {

    private final long startTime;
    private final String requestId;

    public ResponseLoggingInterceptor(ServerHttpResponse delegate, long startTime, String requestId) {
        super(delegate);
        this.startTime = startTime;
        this.requestId = requestId;
    }

    @Override
    @NonNull
    public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
        return super.writeWith(Flux.from(body).doOnNext(dataBuffer -> {
            MDC.put(CustomHeaders.REQUEST_ID.getValue(), requestId);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String bodyResponse = baos.toString();

                log.info("Response[{}ms]: status={}, cookies={}, payload={}", System.currentTimeMillis() - startTime, getStatusCode(), getCookies().toSingleValueMap(), bodyResponse);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
