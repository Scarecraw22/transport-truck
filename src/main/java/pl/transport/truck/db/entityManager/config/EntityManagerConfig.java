package pl.transport.truck.db.entityManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.transport.truck.db.entityManager.CustomerEntityManager;
import pl.transport.truck.db.entityManager.CustomerEntityManagerImpl;
import pl.transport.truck.db.repository.UserDetailsRepository;
import pl.transport.truck.db.repository.UserRepository;

@Configuration
public class EntityManagerConfig {

    @Bean
    public CustomerEntityManager customerEntityManager(UserRepository userRepository,
                                                       UserDetailsRepository userDetailsRepository) {
        return new CustomerEntityManagerImpl(userRepository, userDetailsRepository);
    }
}
