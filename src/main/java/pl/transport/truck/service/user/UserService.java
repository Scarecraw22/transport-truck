package pl.transport.truck.service.user;

import pl.transport.truck.rest.model.user.*;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<CreateUserResponse> createCustomer(CreateUserRequest request);

    Mono<GetUserDetailsResponse> getUserDetails(Long customerId);

    Mono<LoginUserResponse> loginUser(LoginUserRequest request);
}
