package pl.transport.truck.service.password;

import lombok.NonNull;

public interface PasswordService {

    String encode(@NonNull final String rawPassword, @NonNull final String salt);

    boolean matches(@NonNull final String rawPassword,
                    @NonNull final String salt,
                    @NonNull final String hashedPassword);

    String generateSalt();
}
