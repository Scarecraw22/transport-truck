package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.DriverPhoneEntity;
import reactor.core.publisher.Mono;

public interface DriverPhoneRepository {

    Mono<DriverPhoneEntity> save(DriverPhoneEntity entity);

    Mono<DriverPhoneEntity> delete(DriverPhoneEntity entity);
}
