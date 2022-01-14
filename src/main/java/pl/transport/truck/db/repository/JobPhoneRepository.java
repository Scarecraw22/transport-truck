package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.JobPhoneEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface JobPhoneRepository {

    Mono<JobPhoneEntity> save(JobPhoneEntity entity);

    Flux<JobPhoneEntity> saveAll(Collection<JobPhoneEntity> entities);

    Mono<JobPhoneEntity> delete(JobPhoneEntity entity);
}
