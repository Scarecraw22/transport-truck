package pl.transport.truck.rest.controller

import groovy.util.logging.Slf4j
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import pl.transport.truck.config.TestWebFluxSecurityConfig
import pl.transport.truck.db.repository.UserRepository
import pl.transport.truck.initializer.DbIntegrationTestInitializer
import pl.transport.truck.initializer.RedisIntegrationTestInitializer
import pl.transport.truck.rest.model.customer.CreateCustomerRequest
import pl.transport.truck.rest.model.customer.CreateCustomerResponse
import pl.transport.truck.rest.model.customer.GetCustomerDetailsResponse
import pl.transport.truck.rest.utils.RestConsts
import pl.transport.truck.utils.StringConsts
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Collectors

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        initializers = [RedisIntegrationTestInitializer.class, DbIntegrationTestInitializer.class])
@Import(TestWebFluxSecurityConfig.class)
class CustomerControllerTest extends Specification {

    private static AtomicBoolean migrated = new AtomicBoolean(false)

    @Autowired
    private Flyway flyway

    @Autowired
    private UserRepository userRepository

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

        then:
        Flux<GetCustomerDetailsResponse> customerDetails = client.get()
                .uri("/transport-truck/customer/${newCustomerId.get()}")
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

        and: "Delete user"
        StepVerifier.create(userRepository.deleteById(newCustomerId.get()).log())
                .verifyComplete()
    }
}
