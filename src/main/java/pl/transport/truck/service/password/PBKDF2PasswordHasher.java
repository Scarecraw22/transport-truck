package pl.transport.truck.service.password;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@RequiredArgsConstructor
public class PBKDF2PasswordHasher implements PasswordHasher {

    private static final int ITERATIONS = 1000;
    private static final int HASH_WIDTH = 128;
    private static final String PBKDF2_NAME = "PBKDF2WithHmacSHA512";

    private final PepperProvider pepperProvider;

    public String hash(@NotNull final String password) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), pepperProvider.get().getBytes(), ITERATIONS, HASH_WIDTH);
            SecretKey secretKey = secretKeyFactory.generateSecret(spec);
            return Hex.encodeHexString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error while hashing password: ", e);
            throw new IllegalStateException(e);
        }
    }
}
