package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.CustomerDetailsEntity;
import reactor.core.publisher.Mono;

public interface CustomerDetailsRepository {

    Mono<CustomerDetailsEntity> getCustomerDetails(Long customerId);
}
