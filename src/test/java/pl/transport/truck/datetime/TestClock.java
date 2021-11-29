package pl.transport.truck.datetime;

import pl.transport.truck.datetime.utils.DateTimeConsts;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class TestClock extends Clock {

    @Override
    public ZoneId getZone() {
        return DateTimeConsts.EUROPE_WARSAW_ZONE;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.fixed(instant(), getZone());
    }

    @Override
    public Instant instant() {
        return Instant.parse("2022-01-01T08:00:00.00Z");
    }
}
