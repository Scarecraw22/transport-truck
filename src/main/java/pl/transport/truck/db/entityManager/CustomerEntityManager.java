package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import reactor.core.publisher.Mono;

public interface CustomerEntityManager extends BaseEntityManager<UserEntity> {

    Mono<UserDetailsEntity> getCustomerDetails(Long customerId);
}
