package pl.transport.truck.rest.model.customer;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateCustomerResponse {

    Long id;

}
