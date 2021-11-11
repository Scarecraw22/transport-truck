package pl.transport.truck.db.utils;

import lombok.experimental.UtilityClass;
import pl.transport.truck.utils.StringConsts;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class DbUtils {

    public String getHostFromUrl(String url) {
        return splitUrl(url).get(2).replaceAll(StringConsts.SLASH, StringConsts.EMPTY_STRING);
    }

    public String getPortFromUrl(String url) {
        String port = splitUrl(url).get(3);
        int indexOfFirstSlash = port.indexOf(StringConsts.SLASH);

        if (indexOfFirstSlash != -1) {
            return port.substring(0, indexOfFirstSlash);
        }
        return port;
    }

    private List<String> splitUrl(String url) {
        return Arrays.asList(url.split(StringConsts.COLON));
    }
}
