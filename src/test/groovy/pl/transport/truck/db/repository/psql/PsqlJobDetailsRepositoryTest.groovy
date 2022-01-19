package pl.transport.truck.db.repository.psql

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.*
import pl.transport.truck.db.repository.JobRepository
import pl.transport.truck.db.repository.PhoneNumberRepository
import pl.transport.truck.db.repository.UserRepository
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class PsqlJobDetailsRepositoryTest extends RepositorySpecification {

    @Autowired
    private JobRepository jobRepository

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    @Autowired
    private PsqlJobPhoneRepository jobPhoneRepository

    @Autowired
    private PsqlJobDetailsRepository jobDetailsRepository

    @Autowired
    private UserRepository userRepository

    def "test if Job with PhoneNumber and User is properly retrieved"() {
        given:
        UserEntity user = UserEntity.builder()
                .username("u1")
                .password("password")
                .salt("salt")
                .firstName("f1")
                .lastName("l1")
                .address("a1")
                .email("e1")
                .role("r1")
                .updatedAt(now)
                .createdAt(now)
                .build()

        when: "user is saved"
        AtomicLong userId = new AtomicLong()
        StepVerifier.create(userRepository.save(user).log())
                .consumeNextWith(c -> {
                    userId.set(c.getId())
                })
                .verifyComplete()

        and: "JobEntity is saved"
        AtomicLong jobId = new AtomicLong()
        JobEntity jobEntity = JobEntity.builder()
                .customerId(userId.get())
                .title("title")
                .description("description")
                .sourceAddress("source address")
                .destinationAddress("destination address")
                .destinationEmail("destination email")
                .updatedAt(now)
                .createdAt(now)
                .build()
        StepVerifier.create(jobRepository.save(jobEntity).log())
                .consumeNextWith(job -> {
                    jobId.set(job.getId())
                })
                .verifyComplete()

        and: "PhoneNumberEntity is saved"
        AtomicLong phoneNumberId = new AtomicLong()
        PhoneNumberEntity phoneNumberEntity = PhoneNumberEntity.builder()
                .phonePrefix("48")
                .phoneNumber("777777777")
                .updatedAt(now)
                .createdAt(now)
                .build()
        StepVerifier.create(phoneNumberRepository.save(phoneNumberEntity).log())
                .consumeNextWith(pn -> {
                    phoneNumberId.set(pn.getId())
                })
                .verifyComplete()

        and: "Job with phone is linked"
        JobPhoneEntity jobPhoneEntity = JobPhoneEntity.builder()
                .jobId(jobId.get())
                .phoneNumberId(phoneNumberId.get())
                .build()
        StepVerifier.create(jobPhoneRepository.save(jobPhoneEntity).log())
                .expectNextCount(1)
                .verifyComplete()

        then:
        Mono<JobDetailsEntity> jobDetails = jobDetailsRepository.getJobDetails(jobId.get())
        StepVerifier.create(jobDetails.log())
                .assertNext(jd -> {
                    assert jd.getId() == jobId.get()
                    assert jd.getTitle() == "title"
                    assert jd.getDescription() == "description"
                    assert jd.getSourceAddress() == "source address"
                    assert jd.getDestinationAddress() == "destination address"
                    assert jd.getDestinationEmail() == "destination email"
                    assert jd.getUpdatedAt().isEqual(now)
                    assert jd.getCreatedAt().isEqual(now)
                    UserEntity c = jd.getCustomer()
                    assert c.getId() == userId.get()
                    assert c.getPassword() == "password"
                    assert c.getFirstName() == "f1"
                    assert c.getLastName() == "l1"
                    assert c.getAddress() == "a1"
                    assert c.getEmail() == "e1"
                    assert c.getUpdatedAt().isEqual(now)
                    assert c.getCreatedAt().isEqual(now)
                    assert jd.getPhones().size() == 1
                    jd.getPhones().forEach(pn -> {
                        assert pn.getId() == phoneNumberId.get()
                        assert pn.getPhonePrefix() == "48"
                        assert pn.getPhoneNumber() == "777777777"
                        assert pn.getCreatedAt().isEqual(now)
                        assert pn.getUpdatedAt().isEqual(now)

                    })
                })
                .verifyComplete()

        cleanup:
        testRepositoryUtils.deleteJobById(jobEntity.getId())
        testRepositoryUtils.deleteUserById(userId.get())
    }
}
