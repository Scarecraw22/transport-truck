package pl.transport.truck.rest.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class RestConsts {

    public final List<String> ALLOWED_HEADERS = List.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers",
            "Origin",
            "X-Requested-With",
            "Content-Type",
            "Accept",
            "Authorization"
    );
    public final List<String> ALLOWED_HTTP_METHODS = Stream.of(HttpMethod.values()).map(HttpMethod::name).collect(Collectors.toList());
}
