package pl.transport.truck.service.job;

import pl.transport.truck.rest.model.job.CreateJobRequest;
import pl.transport.truck.rest.model.job.CreateJobResponse;
import reactor.core.publisher.Mono;

public interface JobService {

    Mono<CreateJobResponse> createJob(CreateJobRequest request);

}
