package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.CustomerDetailsEntity;
import pl.transport.truck.db.entity.CustomerEntity;
import reactor.core.publisher.Mono;

public interface CustomerEntityManager extends BaseEntityManager<CustomerEntity> {

    Mono<CustomerDetailsEntity> getCustomerDetails(Long customerId);
}
