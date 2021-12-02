package pl.transport.truck.rest.model.customer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder
@Jacksonized
public class CreateCustomerRequest {

    String password;
    String firstName;
    String lastName;
    String email;
    String address;
    Set<Phone> phones;

    @Value
    @Builder
    @Jacksonized
    public static class Phone {
        String prefix;
        String number;
    }
}
