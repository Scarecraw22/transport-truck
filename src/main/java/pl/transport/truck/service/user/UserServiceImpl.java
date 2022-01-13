package pl.transport.truck.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import pl.transport.truck.converter.UserModelConverter;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.entityManager.PhoneNumberEntityManager;
import pl.transport.truck.db.entityManager.UserEntityManager;
import pl.transport.truck.rest.jwt.JwtSupportService;
import pl.transport.truck.rest.model.user.*;
import pl.transport.truck.service.common.EntityTimestampService;
import pl.transport.truck.service.ex.UserNotFoundException;
import pl.transport.truck.service.password.PasswordService;
import pl.transport.truck.service.password.ex.WrongPasswordException;
import pl.transport.truck.utils.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserEntityManager userEntityManager;
    private final UserModelConverter userModelConverter;
    private final EntityTimestampService entityTimestampService;
    private final PasswordService passwordService;
    private final ReactiveUserDetailsService userDetailsService;
    private final JwtSupportService jwtSupportService;
    private final PhoneNumberEntityManager phoneNumberEntityManager;

    @Override
    public Mono<CreateUserResponse> createCustomer(CreateUserRequest request) {
        return Mono.just(request)
                .map(userModelConverter::convertToCustomerEntity)
                .map(customerEntity -> {
                    log.info("Generating password for user: {}", request.getUsername());
                    String salt = passwordService.generateSalt();
                    customerEntity.setSalt(salt);
                    customerEntity.setPassword(passwordService.encode(customerEntity.getPassword(), salt));
                    entityTimestampService.setupDatesForNewEntity(customerEntity);
                    return customerEntity;
                })
                .flatMap(userEntityManager::save)
                .map(user -> {
                    if (CollectionUtils.isNotEmpty(request.getPhones())) {
                        Flux.fromIterable(request.getPhones())
                                .map(phone -> PhoneNumberEntity.builder()
                                        .phoneNumber(phone.getNumber())
                                        .phonePrefix(phone.getPrefix())
                                        .build())
                                .map(phoneNumberEntity -> {
                                    entityTimestampService.setupDatesForNewEntity(phoneNumberEntity);
                                    return phoneNumberEntity;
                                })
                                .flatMap(phoneNumberEntityManager::save)
                                .doOnNext(phoneNumberEntity -> {
                                    log.info("Adding PhoneNumber: [{}], to user with id: [{}]", phoneNumberEntity, user.getId());

                                    userEntityManager.addPhoneToUser(user.getId(), phoneNumberEntity.getId()).subscribe();
                                })
                                .doOnError(error -> log.error("Error while trying to add phones to user: ", error))
                                .doOnComplete(() -> log.info("Successfully added phones: {} to user with id: [{}]", request.getPhones(), user.getId()))
                                .subscribe();

                        return user;
                    }
                    log.info("There is no phone number provided");
                    return user;
                })
                .map(userModelConverter::convertToCreateCustomerResponse);
    }

    @Override
    public Mono<GetUserDetailsResponse> getUserDetails(Long customerId) {
        return userEntityManager.getUserDetails(customerId)
                .map(userModelConverter::convertToCustomerDetailsResponse);
    }

    @Override
    public Mono<LoginUserResponse> loginUser(LoginUserRequest request) {
        if (request == null) {
            return Mono.empty();
        }
        return userDetailsService.findByUsername(request.getUsername())
                .flatMap(userDetails -> userEntityManager.getByUsername(request.getUsername()))
                .map(user -> {
                    if (passwordService.matches(request.getPassword(), user.getSalt(), user.getPassword())) {
                        return LoginUserResponse.builder()
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
