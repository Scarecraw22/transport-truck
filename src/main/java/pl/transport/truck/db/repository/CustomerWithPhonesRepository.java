package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.CustomerWithPhonesEntity;
import reactor.core.publisher.Mono;

public interface CustomerWithPhonesRepository {

    Mono<CustomerWithPhonesEntity> getCustomerWithPhones(Long customerId);
}
