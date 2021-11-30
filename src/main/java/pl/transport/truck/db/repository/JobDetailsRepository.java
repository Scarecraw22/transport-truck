package pl.transport.truck.db.repository;

import pl.transport.truck.db.entity.JobDetailsEntity;
import reactor.core.publisher.Mono;

public interface JobDetailsRepository {

    Mono<JobDetailsEntity> getJobDetails(Long jobId);
}
