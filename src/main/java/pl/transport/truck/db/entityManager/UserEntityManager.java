package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.db.entity.UserPhoneEntity;
import reactor.core.publisher.Mono;

public interface UserEntityManager extends BaseEntityManager<UserEntity> {

    Mono<UserDetailsEntity> getUserDetails(Long customerId);

    Mono<UserEntity> getByUsername(String username);

    Mono<UserPhoneEntity> addPhoneToUser(Long userId, Long phoneNumberId);
}
