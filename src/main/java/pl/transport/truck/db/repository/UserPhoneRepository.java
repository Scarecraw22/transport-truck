package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.UserPhoneEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserPhoneRepository {

    Mono<UserPhoneEntity> save(UserPhoneEntity entity);

    Mono<UserPhoneEntity> delete(UserPhoneEntity entity);

    Flux<UserPhoneEntity> getByUserId(Long userId);
}
