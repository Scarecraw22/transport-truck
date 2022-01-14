package pl.transport.truck.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.transport.truck.rest.model.job.CreateJobResponse;
import pl.transport.truck.rest.model.job.CreateJobRequest;
import pl.transport.truck.service.job.JobService;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transport-truck/job")
public class JobController {

    private final JobService jobService;

    @PostMapping
    public Mono<ResponseEntity<CreateJobResponse>> createCustomer(@RequestBody CreateJobRequest request) {
        return jobService.createJob(request)
                .map(ResponseEntity::ok);
    }
}
