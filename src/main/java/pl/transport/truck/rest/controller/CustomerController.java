package pl.transport.truck.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.transport.truck.rest.model.customer.CreateCustomerRequest;
import pl.transport.truck.rest.model.customer.CreateCustomerResponse;
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse;
import pl.transport.truck.service.customer.CustomerService;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transport-truck/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerId}")
    public Mono<ResponseEntity<GetCustomerDetailsResponse>> getCustomerDetails(@PathVariable Long customerId) {
        return customerService.getCustomerDetails(customerId)
                .map(ResponseEntity::ok);
    }

    @CrossOrigin(methods = {RequestMethod.POST})
    @PostMapping
    public Mono<ResponseEntity<CreateCustomerResponse>> createCustomer(@RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request)
                .map(ResponseEntity::ok);
    }
}
