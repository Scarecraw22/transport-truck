package pl.transport.truck.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.transport.truck.service.password.LocalPepperProvider;
import pl.transport.truck.service.password.PBKDF2PasswordHasher;
import pl.transport.truck.service.password.PasswordHasher;
import pl.transport.truck.service.password.PepperProvider;

@Configuration
public class ServiceConfig {

    @Bean
    public PepperProvider pepperProvider() {
        return new LocalPepperProvider();
    }

    @Bean
    public PasswordHasher passwordHasher(PepperProvider pepperProvider) {
        return new PBKDF2PasswordHasher(pepperProvider);
    }
}
