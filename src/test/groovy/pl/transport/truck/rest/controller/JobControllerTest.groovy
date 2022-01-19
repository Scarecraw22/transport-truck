package pl.transport.truck.rest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import pl.transport.truck.db.repository.JobPhoneRepository
import pl.transport.truck.rest.model.job.CreateJobRequest
import pl.transport.truck.rest.model.job.GetJobDetailsResponse
import pl.transport.truck.rest.model.user.CreateUserRequest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class JobControllerTest extends AbstractControllerTest {

    @Autowired
    private JobPhoneRepository jobPhoneRepository

    def "test if Job is properly created when given phone doesn't exist"() {
        given: "new user is created and logged"
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

        when: "new job is created"
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

        Long newJobId = createJob(createJobRequest, token)

        then: "job details are properly returned"
        Flux<GetJobDetailsResponse> jobDetails = client.get()
                .uri("/transport-truck/job/${newJobId}")
                .header("Origin", "http://any-origin.com")
                .header("Access-Control-Request-Method", "GET")
                .header("X-RequestId", "request-id")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(GetJobDetailsResponse.class)
                .getResponseBody()

        StepVerifier.create(jobDetails.log())
                .assertNext(next -> {
                    assert next.getId() == newJobId
                    assert next.getTitle() == "title"
                    assert next.getDescription() == "description"
                    assert next.getSourceAddress() == "sa1"
                    assert next.getDestinationAddress() == "da1"
                    assert next.getDestinationEmail() == "de1"
                    next.getPhones().forEach(phone -> {
                        assert phone.getNumber() == "123456789"
                        assert phone.getPrefix() == "48"
                    })
                    assert next.getCustomer().getId() == userId
                    assert next.getCustomer().getUsername() == "u1"
                    assert next.getCustomer().getFirstName() == "f1"
                    assert next.getCustomer().getLastName() == "l1"
                    assert next.getCustomer().getEmail() == "e1"
                    assert next.getCustomer().getRole() == "CUSTOMER"
                    assert next.getCustomer().getAddress() == "a1"
                })
    }
}
