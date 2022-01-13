package pl.transport.truck.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.transport.truck.rest.model.user.*;
import pl.transport.truck.service.user.UserService;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/transport-truck/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<GetUserDetailsResponse>> getCustomerDetails(@PathVariable Long userId) {
        return userService.getUserDetails(userId)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginUserResponse>> createCustomer(@RequestBody LoginUserRequest request) {
        return userService.loginUser(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<CreateUserResponse>> createCustomer(@RequestBody CreateUserRequest request) {
        return userService.createCustomer(request)
                .map(ResponseEntity::ok);
    }
}
