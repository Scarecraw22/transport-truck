package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.JobPhoneEntity;
import reactor.core.publisher.Mono;

public interface JobPhoneRepository {

    Mono<JobPhoneEntity> save(JobPhoneEntity entity);

    Mono<JobPhoneEntity> delete(JobPhoneEntity entity);
}
