package pl.transport.truck.datetime;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(value = "pl.transport.truck.fixed-clock", havingValue = "true")
public @interface ConditionalOnFixedClock {
}
