package pl.transport.truck.rest.controller

import groovy.util.logging.Slf4j
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import pl.transport.truck.db.repository.UserRepository
import pl.transport.truck.initializer.DbIntegrationTestInitializer
import pl.transport.truck.initializer.RedisIntegrationTestInitializer
import pl.transport.truck.rest.config.JwtProperties
import pl.transport.truck.rest.model.customer.*
import pl.transport.truck.rest.utils.RestConsts
import pl.transport.truck.utils.StringConsts
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        initializers = [RedisIntegrationTestInitializer.class, DbIntegrationTestInitializer.class])
class CustomerControllerTest extends Specification {

    private static AtomicBoolean migrated = new AtomicBoolean(false)
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private Flyway flyway

    @Autowired
    private UserRepository userRepository

    @Autowired
    private JwtProperties jwtProperties

    def setup() {
        synchronized (this) {
            if (!migrated.get()) {
                migrated.set(true)
                flyway.migrate()
            }
        }
    }

    @Autowired
    private WebTestClient client

    def "test"() {
        given:
        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .username("username")
                .firstName("first")
                .lastName("last")
                .email("email")
                .role("role")
                .address("address")
                .password("password")
                .phones(Set.of())
                .build()

        when:
        Flux<CreateCustomerResponse> response = client.post()
                .uri("/transport-truck/customer")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "POST")
                .header("X-RequestId", UUID.randomUUID().toString())
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                .returnResult(CreateCustomerResponse.class)
                .getResponseBody()

        and:
        AtomicLong newCustomerId = new AtomicLong()
        StepVerifier.create(response.log())
                .consumeNextWith(res -> {
                    newCustomerId.set(res.getId())
                })
                .verifyComplete()

        and:
        LoginCustomerRequest loginRequest = LoginCustomerRequest.builder()
                .username("username")
                .password("password")
                .build()

        Flux<LoginCustomerResponse> loginResponse = client.post()
                .uri("/transport-truck/customer/login")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "POST")
                .header("X-RequestId", UUID.randomUUID().toString())
                .bodyValue(loginRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                .returnResult(LoginCustomerResponse.class)
                .getResponseBody()

        then:
        def token = null
        StepVerifier.create(loginResponse.log())
                .consumeNextWith(next -> {
                    token = next.getToken()
                })
                .verifyComplete()

        Flux<GetCustomerDetailsResponse> customerDetails = client.get()
                .uri("/transport-truck/customer/${newCustomerId.get()}")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "GET")
                .header("X-RequestId", "request-id")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(GetCustomerDetailsResponse.class)
                .getResponseBody()

        and:
        StepVerifier.create(customerDetails.log())
                .assertNext(next -> {
                    assert next.getId() == newCustomerId.get()
                })
                .verifyComplete()

        cleanup: "Delete user"
        StepVerifier.create(userRepository.deleteById(newCustomerId.get()).log())
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
                CreateCustomerRequest request = CreateCustomerRequest.builder()
                        .username(username)
                        .firstName("first")
                        .lastName("last")
                        .email("email")
                        .role("role")
                        .address("address")
                        .password("password")
                        .phones(Set.of())
                        .build()
                Flux<CreateCustomerResponse> response = client.post()
                        .uri("/transport-truck/customer")
                        .header("Origin", "http://any-origin.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("X-RequestId", UUID.randomUUID().toString())
                        .bodyValue(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().is2xxSuccessful()
                        .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                        .returnResult(CreateCustomerResponse.class)
                        .getResponseBody()

                StepVerifier.create(response.log())
                        .consumeNextWith(res -> {
                            println "Customer id: ${res.getId()}"
                        })
                        .verifyComplete()
            }
        }
    }
}
