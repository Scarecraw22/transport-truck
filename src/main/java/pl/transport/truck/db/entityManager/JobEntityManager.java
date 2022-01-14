package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.JobEntity;
import pl.transport.truck.db.entity.JobPhoneEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface JobEntityManager extends BaseEntityManager<JobEntity> {

    Flux<JobPhoneEntity> addPhonesToJob(Collection<JobPhoneEntity> entities);

    Mono<JobPhoneEntity> addPhoneToJob(JobPhoneEntity entity);
}
