package pl.transport.truck.rest.model.phone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;

@Value
@Builder
@Jacksonized
@AllArgsConstructor
public class PhoneNumberDetails {
    Long id;
    String phonePrefix;
    String phoneNumber;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
}
