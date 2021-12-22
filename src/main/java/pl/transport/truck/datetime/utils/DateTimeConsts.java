package pl.transport.truck.datetime.utils;

import lombok.experimental.UtilityClass;

import java.time.ZoneId;

@UtilityClass
public class DateTimeConsts {

    public final String EUROPE_WARSAW = "Europe/Warsaw";
    public final ZoneId EUROPE_WARSAW_ZONE = ZoneId.of(EUROPE_WARSAW);
    public final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
