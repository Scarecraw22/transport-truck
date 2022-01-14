package pl.transport.truck.rest.model.job;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateJobResponse {

    Long jobId;

    public static CreateJobResponse fromJobId(Long jobId) {
        return new CreateJobResponse(jobId);
    }
}
