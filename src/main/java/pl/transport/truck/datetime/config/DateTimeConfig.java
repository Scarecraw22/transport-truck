package pl.transport.truck.datetime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.transport.truck.datetime.utils.DateTimeConsts;

import java.time.Clock;

@Profile("!test")
@Configuration
public class DateTimeConfig {

    @Bean
    public Clock clock() {
        return Clock.system(DateTimeConsts.EUROPE_WARSAW_ZONE);
    }
}
