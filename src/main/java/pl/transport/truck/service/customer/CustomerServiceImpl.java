package pl.transport.truck.service.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.transport.truck.converter.CustomerModelConverter;
import pl.transport.truck.db.entityManager.CustomerEntityManager;
import pl.transport.truck.rest.model.customer.CreateCustomerRequest;
import pl.transport.truck.rest.model.customer.CreateCustomerResponse;
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse;
import pl.transport.truck.service.common.EntityTimestampService;
import pl.transport.truck.service.password.PasswordService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerEntityManager customerEntityManager;
    private final CustomerModelConverter customerModelConverter;
    private final EntityTimestampService entityTimestampService;
    private final PasswordService passwordService;

    @Override
    public Mono<CreateCustomerResponse> createCustomer(CreateCustomerRequest request) {
        return Mono.just(request)
                .map(customerModelConverter::convertToCustomerEntity)
                .map(customerEntity -> {
                    String salt = passwordService.generateSalt();
                    customerEntity.setSalt(salt);
                    customerEntity.setPassword(passwordService.encode(customerEntity.getPassword(), salt));
                    entityTimestampService.setupDatesForNewEntity(customerEntity);
                    return customerEntity;
                })
                .flatMap(customerEntityManager::save)
                .map(customerModelConverter::convertToCreateCustomerResponse);
    }

    @Override
    public Mono<GetCustomerDetailsResponse> getCustomerDetails(Long customerId) {
        return customerEntityManager.getCustomerDetails(customerId)
                .map(customerModelConverter::convertToCustomerDetailsResponse);
    }
}
