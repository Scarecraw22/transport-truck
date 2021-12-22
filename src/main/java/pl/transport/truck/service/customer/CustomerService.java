package pl.transport.truck.service.customer;

import pl.transport.truck.rest.model.customer.*;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Mono<CreateCustomerResponse> createCustomer(CreateCustomerRequest request);

    Mono<GetCustomerDetailsResponse> getCustomerDetails(Long customerId);

    Mono<LoginCustomerResponse> loginCustomer(LoginCustomerRequest request);
}
