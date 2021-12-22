package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import reactor.core.publisher.Mono;

public interface UserEntityManager extends BaseEntityManager<UserEntity> {

    Mono<UserDetailsEntity> getCustomerDetails(Long customerId);

    Mono<UserEntity> getByUsername(String username);
}
