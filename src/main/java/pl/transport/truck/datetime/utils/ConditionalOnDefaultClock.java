package pl.transport.truck.datetime.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(value = "pl.transport.truck.fixed-clock", havingValue = "false", matchIfMissing = true)
public @interface ConditionalOnDefaultClock {
}
