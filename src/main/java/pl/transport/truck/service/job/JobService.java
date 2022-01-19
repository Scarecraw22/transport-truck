package pl.transport.truck.service.job;

import pl.transport.truck.rest.model.job.CreateJobRequest;
import pl.transport.truck.rest.model.job.CreateJobResponse;
import pl.transport.truck.rest.model.job.GetJobDetailsResponse;
import reactor.core.publisher.Mono;

public interface JobService {

    Mono<CreateJobResponse> createJob(CreateJobRequest request);

    Mono<GetJobDetailsResponse> getJobDetails(Long jobId);
}
