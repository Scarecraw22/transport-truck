package pl.transport.truck.service.customer;

import pl.transport.truck.rest.model.customer.CreateCustomerRequest;
import pl.transport.truck.rest.model.customer.CreateCustomerResponse;
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Mono<CreateCustomerResponse> createCustomer(CreateCustomerRequest request);

    Mono<GetCustomerDetailsResponse> getCustomerDetails(Long customerId);
}
