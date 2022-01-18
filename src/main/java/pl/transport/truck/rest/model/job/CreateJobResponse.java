package pl.transport.truck.rest.model.job;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateJobResponse {

    Long jobId;

    public static CreateJobResponse fromJobId(Long jobId) {
        return new CreateJobResponse(jobId);
    }
}
