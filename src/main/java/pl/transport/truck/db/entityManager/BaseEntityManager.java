package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.BaseEntity;
import reactor.core.publisher.Mono;

public interface BaseEntityManager<T extends BaseEntity> {

    Mono<T> getById(Long id);

    Mono<T> save(T entity);

    Mono<T> update(T entity);
}
