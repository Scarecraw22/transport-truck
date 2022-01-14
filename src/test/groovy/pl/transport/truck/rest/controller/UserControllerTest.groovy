package pl.transport.truck.rest.controller

import groovy.util.logging.Slf4j
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import pl.transport.truck.db.entity.UserPhoneEntity
import pl.transport.truck.db.repository.UserPhoneRepository
import pl.transport.truck.db.repository.UserRepository
import pl.transport.truck.initializer.DbIntegrationTestInitializer
import pl.transport.truck.initializer.RedisIntegrationTestInitializer
import pl.transport.truck.rest.model.phone.PhoneNumberDetails
import pl.transport.truck.rest.model.user.*
import pl.transport.truck.rest.utils.RestConsts
import pl.transport.truck.utils.StringConsts
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Collectors

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        initializers = [RedisIntegrationTestInitializer.class, DbIntegrationTestInitializer.class])
class UserControllerTest extends Specification {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserPhoneRepository userPhoneRepository

    @Autowired
    private WebTestClient client

    def "test"() {
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

        and:
        AtomicLong newUserId = new AtomicLong()
        StepVerifier.create(response.log())
                .consumeNextWith(res -> {
                    newUserId.set(res.getId())
                })
                .verifyComplete()

        and:
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username("username")
                .password("password")
                .build()

        Flux<LoginUserResponse> loginResponse = client.post()
                .uri("/transport-truck/user/login")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "POST")
                .header("X-RequestId", UUID.randomUUID().toString())
                .bodyValue(loginRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                .returnResult(LoginUserResponse.class)
                .getResponseBody()

        then:
        def token = null
        StepVerifier.create(loginResponse.log())
                .consumeNextWith(next -> {
                    token = next.getToken()
                })
                .verifyComplete()

        sleep(5000)
        Flux<GetUserDetailsResponse> UserDetails = client.get()
                .uri("/transport-truck/user/${newUserId.get()}")
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
                    assert next.getId() == newUserId.get()
                    assert !next.getPhones().isEmpty()
                })
                .verifyComplete()

        cleanup: "Delete user"
        List<UserPhoneEntity> phonesToDelete = phones.stream()
                .map(phone -> UserPhoneEntity.builder()
                        .userId(newUserId.get())
                        .phoneNumberId(phone.getId())
                        .build())
                .collect(Collectors.toList())

        Flux.fromIterable(phonesToDelete).log()
                .map(phone -> userPhoneRepository.delete(phone).toFuture().get())
                .subscribe()

        StepVerifier.create(userRepository.deleteById(newUserId.get()).log())
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
