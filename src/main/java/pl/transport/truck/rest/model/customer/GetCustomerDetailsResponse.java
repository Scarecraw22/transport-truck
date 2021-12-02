package pl.transport.truck.rest.model.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.transport.truck.rest.model.phone.PhoneNumberDetails;

import java.time.ZonedDateTime;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class GetCustomerDetailsResponse {

    Long id;
    String firstName;
    String lastName;
    String address;
    String email;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
    Set<PhoneNumberDetails> phones;

}
