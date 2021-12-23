package pl.transport.truck.service.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import pl.transport.truck.converter.CustomerModelConverter;
import pl.transport.truck.db.entityManager.UserEntityManager;
import pl.transport.truck.rest.jwt.JwtSupportService;
import pl.transport.truck.rest.model.customer.*;
import pl.transport.truck.service.common.EntityTimestampService;
import pl.transport.truck.service.ex.UserNotFoundException;
import pl.transport.truck.service.password.PasswordService;
import pl.transport.truck.service.password.ex.WrongPasswordException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserEntityManager userEntityManager;
    private final CustomerModelConverter customerModelConverter;
    private final EntityTimestampService entityTimestampService;
    private final PasswordService passwordService;
    private final ReactiveUserDetailsService userDetailsService;
    private final JwtSupportService jwtSupportService;

    @Override
    public Mono<CreateCustomerResponse> createCustomer(CreateCustomerRequest request) {
        return Mono.just(request)
                .map(customerModelConverter::convertToCustomerEntity)
                .map(customerEntity -> {
                    log.info("Generating password for user: {}", request.getUsername());
                    String salt = passwordService.generateSalt();
                    customerEntity.setSalt(salt);
                    customerEntity.setPassword(passwordService.encode(customerEntity.getPassword(), salt));
                    entityTimestampService.setupDatesForNewEntity(customerEntity);
                    return customerEntity;
                })
                .flatMap(userEntityManager::save)
                .map(customerModelConverter::convertToCreateCustomerResponse);
    }

    @Override
    public Mono<GetCustomerDetailsResponse> getCustomerDetails(Long customerId) {
        return userEntityManager.getCustomerDetails(customerId)
                .map(customerModelConverter::convertToCustomerDetailsResponse);
    }

    @Override
    public Mono<LoginCustomerResponse> loginCustomer(LoginCustomerRequest request) {
        if (request == null) {
            return Mono.empty();
        }
        return userDetailsService.findByUsername(request.getUsername())
                .flatMap(userDetails -> userEntityManager.getByUsername(request.getUsername()))
                .map(user -> {
                    if (passwordService.matches(request.getPassword(), user.getSalt(), user.getPassword())) {
                        return LoginCustomerResponse.builder()
                                .token(jwtSupportService.generate(user.getUsername()).getValue())
                                .build();
                    } else {
                        throw new WrongPasswordException("Password doesn't match for username: " + request.getUsername());
                    }
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + request.getUsername() + " doesn't exist")))
                .doOnError(throwable -> log.error("Error while trying to save client: {}, exception: ", request.getUsername(), throwable));
    }
}
