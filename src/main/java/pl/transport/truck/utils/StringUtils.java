package pl.transport.truck.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class StringUtils {

    public boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public String generateRandomString() {
        return RandomStringUtils.random(20, true, true);
    }
}
