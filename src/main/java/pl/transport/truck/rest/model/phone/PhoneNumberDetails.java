package pl.transport.truck.rest.model.phone;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@AllArgsConstructor
public class PhoneNumberDetails {
    Long id;
    String phonePrefix;
    String phoneNumber;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
}
