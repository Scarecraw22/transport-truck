package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.UserDetailsEntity;
import reactor.core.publisher.Mono;

public interface UserDetailsRepository {

    Mono<UserDetailsEntity> getUserDetails(Long customerId);

}
