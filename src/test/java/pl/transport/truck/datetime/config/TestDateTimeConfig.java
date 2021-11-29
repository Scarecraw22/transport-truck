package pl.transport.truck.datetime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.transport.truck.datetime.TestClock;

import java.time.Clock;

@Profile("test")
@Configuration
public class TestDateTimeConfig {

    @Bean
    public Clock clock() {
        return new TestClock();
    }
}
