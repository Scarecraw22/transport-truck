package pl.transport.truck.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}
