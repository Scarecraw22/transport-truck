package pl.transport.truck.rest.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

@Slf4j
public class RequestLoggingInterceptor extends ServerHttpRequestDecorator {

    public RequestLoggingInterceptor(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String body = baos.toString();
                log.info("Request: method={}, uri={}, payload={}", getDelegate().getMethod(), getDelegate().getPath(), body);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
