package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.DriverWithPhonesEntity;
import reactor.core.publisher.Mono;

public interface DriverWithPhonesRepository {

    Mono<DriverWithPhonesEntity> getCustomerWithPhones(Long driverId);
}
