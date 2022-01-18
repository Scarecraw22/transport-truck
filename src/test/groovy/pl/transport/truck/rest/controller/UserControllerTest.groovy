package pl.transport.truck.rest.controller

import groovy.util.logging.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import pl.transport.truck.db.entity.UserPhoneEntity
import pl.transport.truck.rest.model.phone.PhoneNumberDetails
import pl.transport.truck.rest.model.user.*
import pl.transport.truck.rest.utils.RestConsts
import pl.transport.truck.utils.StringConsts
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors

@Slf4j
class UserControllerTest extends AbstractControllerTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    def "test if user is properly created and is able to login"() {
        given:
        CreateUserRequest request = CreateUserRequest.builder()
                .username("username")
                .firstName("first")
                .lastName("last")
                .email("email")
                .role("role")
                .address("address")
                .password("password")
                .phones(Set.of(
                        CreateUserRequest.Phone.builder()
                                .number("111111111")
                                .prefix("48")
                                .build(),
                        CreateUserRequest.Phone.builder()
                                .number("222222222")
                                .prefix("48")
                                .build(),
                ))
                .build()

        when:
        Long newUserId = createUser(request)
        def token = loginUser("username", "password")

        then:
        sleep(5000)
        Flux<GetUserDetailsResponse> UserDetails = client.get()
                .uri("/transport-truck/user/${newUserId}")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "GET")
                .header("X-RequestId", "request-id")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(GetUserDetailsResponse.class)
                .getResponseBody()

        and:
        List<PhoneNumberDetails> phones = []
        StepVerifier.create(UserDetails.log())
                .assertNext(next -> {
                    phones = new ArrayList<>(next.getPhones())
                    assert next.getId() == newUserId
                    assert !next.getPhones().isEmpty()
                })
                .verifyComplete()

        cleanup: "Delete user"
        List<UserPhoneEntity> phonesToDelete = phones.stream()
                .map(phone -> UserPhoneEntity.builder()
                        .userId(newUserId)
                        .phoneNumberId(phone.getId())
                        .build())
                .collect(Collectors.toList())

        Flux.fromIterable(phonesToDelete).log()
                .map(phone -> userPhoneRepository.delete(phone).toFuture().get())
                .subscribe()

        StepVerifier.create(userRepository.deleteById(newUserId).log())
                .verifyComplete()
    }

    // Test for checking if each request has proper logs and MDC data is not shuffled
    def "logging test with 2 request at once"() {
        when:
        def runnable1 = createUser("unique1")
        def runnable2 = createUser("unique2")
        def runnable3 = createUser("unique3")
        executorService.execute(runnable1)
        executorService.execute(runnable2)
        executorService.execute(runnable3)

        then:
        sleep(5000)
        noExceptionThrown()
    }

    private Runnable createUser(String username) {
        return new Runnable() {
            @Override
            void run() {
                CreateUserRequest request = CreateUserRequest.builder()
                        .username(username)
                        .firstName("first")
                        .lastName("last")
                        .email("email")
                        .role("role")
                        .address("address")
                        .password("password")
                        .phones(Set.of())
                        .build()
                Flux<CreateUserResponse> response = client.post()
                        .uri("/transport-truck/user")
                        .header("Origin", "http://any-origin.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("X-RequestId", UUID.randomUUID().toString())
                        .bodyValue(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().is2xxSuccessful()
                        .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                        .returnResult(CreateUserResponse.class)
                        .getResponseBody()

                StepVerifier.create(response.log())
                        .consumeNextWith(res -> {
                            println "User id: ${res.getId()}"

                            StepVerifier.create(userRepository.deleteById(res.getId()).log())
                                    .verifyComplete()
                        })
                        .verifyComplete()
            }
        }
    }
}
