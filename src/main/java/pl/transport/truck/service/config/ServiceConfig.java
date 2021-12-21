package pl.transport.truck.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import pl.transport.truck.service.password.LocalPepperProvider;
import pl.transport.truck.service.password.PepperProvider;

@Configuration
public class ServiceConfig {

    @Bean
    public PepperProvider pepperProvider() {
        return new LocalPepperProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder(PepperProvider pepperProvider) {
        return new Pbkdf2PasswordEncoder(pepperProvider.get());
    }

//    @Bean
//    public CustomerService customerService(CustomerEntityManager customerEntityManager,
//                                           CustomerModelConverter customerModelConverter) {
//        return new CustomerServiceImpl(customerEntityManager, customerModelConverter);
//    }
}
