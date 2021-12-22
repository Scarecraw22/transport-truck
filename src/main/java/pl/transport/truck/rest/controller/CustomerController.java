package pl.transport.truck.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.transport.truck.rest.model.customer.*;
import pl.transport.truck.service.customer.CustomerService;
import reactor.core.publisher.Mono;

@CrossOrigin
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

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginCustomerResponse>> createCustomer(@RequestBody LoginCustomerRequest request) {
        return customerService.loginCustomer(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<CreateCustomerResponse>> createCustomer(@RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request)
                .map(ResponseEntity::ok);
    }
}
