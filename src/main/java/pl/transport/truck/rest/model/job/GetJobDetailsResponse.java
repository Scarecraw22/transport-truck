package pl.transport.truck.rest.model.job;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class GetJobDetailsResponse {

    Long id;
    String title;
    String description;
    String sourceAddress;
    String destinationAddress;
    String destinationEmail;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
    User customer;
    Set<Phone> phones;

    @Value
    @Builder
    @Jacksonized
    public static class User {
        Long id;
        String username;
        String firstName;
        String lastName;
        String address;
        String email;
        String role;
    }

    @Value
    @Builder
    @Jacksonized
    public static class Phone {
        Long id;
        String prefix;
        String number;
    }
}
