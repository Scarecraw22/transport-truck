package pl.transport.truck.db.entityManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.transport.truck.db.entityManager.CustomerEntityManager;
import pl.transport.truck.db.entityManager.CustomerEntityManagerImpl;
import pl.transport.truck.db.repository.CustomerDetailsRepository;
import pl.transport.truck.db.repository.CustomerRepository;

@Configuration
public class EntityManagerConfig {

    @Bean
    public CustomerEntityManager customerEntityManager(CustomerRepository customerRepository,
                                                       CustomerDetailsRepository customerDetailsRepository) {
        return new CustomerEntityManagerImpl(customerRepository, customerDetailsRepository);
    }
}
