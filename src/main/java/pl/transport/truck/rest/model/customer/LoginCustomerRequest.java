package pl.transport.truck.rest.model.customer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder
@Jacksonized
public class LoginCustomerRequest {

    String username;
    String password;
}
