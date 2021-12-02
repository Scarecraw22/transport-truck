package pl.transport.truck.service.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.transport.truck.converter.CustomerModelConverter;
import pl.transport.truck.db.entity.CustomerEntity;
import pl.transport.truck.db.entityManager.CustomerEntityManager;
import pl.transport.truck.rest.model.customer.CreateCustomerRequest;
import pl.transport.truck.rest.model.customer.CreateCustomerResponse;
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service()
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerEntityManager customerEntityManager;
    private final CustomerModelConverter customerModelConverter;

    @Override
    public Mono<CreateCustomerResponse> createCustomer(CreateCustomerRequest request) {
        return Mono.just(request)
                .map(customerModelConverter::convertToCustomerEntity)
                .flatMap(customerEntityManager::save)
                .map(customerModelConverter::convertToCreateCustomerResponse);
    }

    @Override
    public Mono<GetCustomerDetailsResponse> getCustomerDetails(Long customerId) {
        return customerEntityManager.getCustomerDetails(customerId)
                .map(customerModelConverter::convertToCustomerDetailsResponse);
    }
}
