package pl.transport.truck.service.md5;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class Md5Service {

    private final MessageDigest md;

    public Md5Service() {
        try {
            this.md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Should never happen: ", e);
            throw new IllegalStateException(e);
        }
    }

    public String hashWithMd5(@NotNull final String password) {
        md.update(password.getBytes());
        return Hex.encodeHexString(md.digest());
    }
}
