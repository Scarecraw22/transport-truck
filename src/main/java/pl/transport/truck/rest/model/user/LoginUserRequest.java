package pl.transport.truck.rest.model.user;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LoginUserRequest {

    String username;
    String password;
}
