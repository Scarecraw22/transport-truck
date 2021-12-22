package pl.transport.truck.rest.jwt.ex;

import org.springframework.security.core.AuthenticationException;

public class InvalidBearerTokenException extends AuthenticationException {

    public InvalidBearerTokenException(String message) {
        super(message);
    }
}
