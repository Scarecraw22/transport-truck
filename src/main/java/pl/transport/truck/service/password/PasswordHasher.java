package pl.transport.truck.service.password;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;

public interface PasswordHasher {

    String hash(@NotNull final String password);
}
