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
import pl.transport.truck.db.repository.*
import pl.transport.truck.initializer.DbIntegrationTestInitializer
import pl.transport.truck.initializer.RedisIntegrationTestInitializer
import pl.transport.truck.rest.model.job.CreateJobRequest
import pl.transport.truck.rest.model.job.CreateJobResponse
import pl.transport.truck.rest.model.user.CreateUserRequest
import pl.transport.truck.rest.model.user.CreateUserResponse
import pl.transport.truck.rest.model.user.LoginUserRequest
import pl.transport.truck.rest.model.user.LoginUserResponse
import pl.transport.truck.rest.utils.RestConsts
import pl.transport.truck.utils.StringConsts
import pl.transport.truck.utils.TestRepositoryUtils
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicLong

@Slf4j
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        initializers = [RedisIntegrationTestInitializer.class, DbIntegrationTestInitializer.class])
abstract class AbstractControllerTest extends Specification {

    @Autowired
    protected TestRepositoryUtils testRepositoryUtils

    @Autowired
    protected UserRepository userRepository

    @Autowired
    protected UserPhoneRepository userPhoneRepository

    @Autowired
    protected JobPhoneRepository jobPhoneRepository

    @Autowired
    protected PhoneNumberRepository phoneNumberRepository

    @Autowired
    protected JobRepository jobRepository

    @Autowired
    protected WebTestClient client

    /**
     * Creates new user and returns it's id
     *
     * @param request
     * @return
     */
    protected Long createUser(CreateUserRequest request) {
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

        AtomicLong newUserId = new AtomicLong()
        StepVerifier.create(response.log())
                .consumeNextWith(res -> {
                    newUserId.set(res.getId())
                })
                .verifyComplete()

        return newUserId.get()
    }

    protected String loginUser(String username, String password) {
        LoginUserRequest loginRequest = LoginUserRequest.builder()
                .username(username)
                .password(password)
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

        def token = null
        StepVerifier.create(loginResponse.log())
                .consumeNextWith(next -> {
                    token = next.getToken()
                })
                .verifyComplete()

        return token
    }

    protected Long createJob(CreateJobRequest request, String token) {
        Flux<CreateJobResponse> createJobResponse = client.post()
                .uri("/transport-truck/job")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "POST")
                .header("X-RequestId", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                .returnResult(CreateJobResponse.class)
                .getResponseBody()

        AtomicLong newJobId = new AtomicLong()
        StepVerifier.create(createJobResponse.log())
                .consumeNextWith(next -> {
                    newJobId.set(next.getJobId())
                    assert next.getJobId() > 0
                })
                .verifyComplete()

        return newJobId.get()
    }

    protected void deleteUserById(Long userId) {
        List<Long> phonesToDelete = unlinkPhonesFromUser(userId)
        Flux<Void> flux = Flux.fromIterable(phonesToDelete)
                .flatMap(id -> phoneNumberRepository.deleteById(id))
                .log()

        StepVerifier.create(flux)
                .verifyComplete()

        StepVerifier.create(userRepository.deleteById(userId).log())
                .verifyComplete()
    }

    protected List<Long> unlinkPhonesFromUser(Long userId) {
        return userPhoneRepository.getByUserId(userId)
                .flatMap(userPhone -> userPhoneRepository.delete(userPhone))
                .map(userPhone -> userPhone.getPhoneNumberId())
                .collectList()
                .toFuture()
                .get()
    }

    protected List<Long> unlinkPhonesFromJob(Long jobId) {
        return jobPhoneRepository.getByJobId(jobId)
                .flatMap(jobPhone -> jobPhoneRepository.delete(jobPhone))
                .map(jobPhone -> jobPhone.getPhoneNumberId())
                .collectList()
                .toFuture()
                .get()
    }

    protected void deleteJobById(Long jobId) {
        List<Long> phonesToDelete = unlinkPhonesFromJob(jobId)
        Flux<Void> flux = Flux.fromIterable(phonesToDelete)
                .flatMap(id -> phoneNumberRepository.deleteById(id))
                .log()

        StepVerifier.create(flux)
                .verifyComplete()

        StepVerifier.create(jobRepository.deleteById(jobId).log())
                .verifyComplete()
    }
}
