package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.CustomerPhoneEntity;
import reactor.core.publisher.Mono;

public interface CustomerPhoneRepository {

    Mono<CustomerPhoneEntity> save(CustomerPhoneEntity entity);

    Mono<CustomerPhoneEntity> delete(CustomerPhoneEntity entity);
}
