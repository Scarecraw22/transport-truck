package pl.transport.truck.rest.model.user;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateUserResponse {

    Long id;

}
