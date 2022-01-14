package pl.transport.truck.rest.model.job;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder
@Jacksonized
public class CreateJobRequest {

    Long customerId;
    String title;
    String description;
    String sourceAddress;
    String destinationAddress;
    String destinationEmail;
    Set<PhoneNumber> jobPhones;

    @Value
    @Builder
    @Jacksonized
    public static class PhoneNumber {
        String prefix;
        String number;
    }
}
