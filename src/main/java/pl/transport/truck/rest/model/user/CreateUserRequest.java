package pl.transport.truck.rest.model.user;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder
@Jacksonized
public class CreateUserRequest {

    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String role;
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
