package pl.transport.truck.db.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.UserEntity;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    @Query("SELECT * FROM USERS WHERE username = :username")
    Mono<UserEntity> findByUsername(String username);

}
