package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.DriverDetailsEntity;
import reactor.core.publisher.Mono;

public interface DriverDetailsRepository {

    Mono<DriverDetailsEntity> getCustomerDetails(Long driverId);
}
