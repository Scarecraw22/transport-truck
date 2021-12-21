package pl.transport.truck.service.password;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.transport.truck.service.md5.Md5Service;
import pl.transport.truck.utils.StringUtils;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final Md5Service md5Service;

    @Override
    public String encode(@NonNull String rawPassword, @NonNull String salt) {
        return passwordEncoder.encode(rawPassword + salt);
    }

    @Override
    public boolean matches(@NonNull final String rawPassword,
                           @NonNull final String salt,
                           @NonNull final String encodedPassword) {

        return passwordEncoder.matches(rawPassword + salt, encodedPassword);
    }

    @Override
    public String generateSalt() {
        return md5Service.hashWithMd5(StringUtils.generateRandomString());
    }
}
