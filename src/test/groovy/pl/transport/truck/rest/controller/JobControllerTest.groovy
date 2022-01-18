package pl.transport.truck.rest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import pl.transport.truck.db.repository.JobPhoneRepository
import pl.transport.truck.rest.model.job.CreateJobRequest
import pl.transport.truck.rest.model.job.CreateJobResponse
import pl.transport.truck.rest.model.user.CreateUserRequest
import pl.transport.truck.rest.utils.RestConsts
import pl.transport.truck.utils.StringConsts
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class JobControllerTest extends AbstractControllerTest {

    @Autowired
    private JobPhoneRepository jobPhoneRepository

    def "test if Job is properly created when given phone doesn't exist"() {
        given:
        CreateUserRequest request = CreateUserRequest.builder()
                .username("u1")
                .firstName("f1")
                .lastName("l1")
                .email("e1")
                .role("CUSTOMER")
                .address("a1")
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

        Long userId = createUser(request)
        String token = loginUser("u1", "password")

        when:
        CreateJobRequest createJobRequest = CreateJobRequest.builder()
                .customerId(userId)
                .title("title")
                .description("description")
                .sourceAddress("sa1")
                .destinationAddress("da1")
                .destinationEmail("de1")
                .jobPhones(Set.of(
                        CreateJobRequest.PhoneNumber.builder()
                                .prefix("48")
                                .number("123456789")
                                .build()
                ))
                .build()

        Flux<CreateJobResponse> createJobResponse = client.post()
                .uri("/transport-truck/job")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "POST")
                .header("X-RequestId", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .bodyValue(createJobRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(StringConsts.COMMA_WITH_SPACE, RestConsts.ALLOWED_HTTP_METHODS))
                .returnResult(CreateJobResponse.class)
                .getResponseBody()

        then:
        AtomicLong newJobId = new AtomicLong()
        StepVerifier.create(createJobResponse.log())
                .consumeNextWith(next -> {
                    newJobId.set(next.getJobId())
                    assert next.getJobId() > 0
                })
                .verifyComplete()

        // TODO add JobDetails endpoint and get details about the job and check if phone numbers are properly saved
    }
}
