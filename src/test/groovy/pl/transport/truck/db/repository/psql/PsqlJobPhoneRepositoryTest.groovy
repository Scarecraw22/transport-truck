package pl.transport.truck.db.repository.psql

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.JobEntity
import pl.transport.truck.db.entity.JobPhoneEntity
import pl.transport.truck.db.entity.PhoneNumberEntity
import pl.transport.truck.db.entity.UserEntity
import pl.transport.truck.db.repository.JobRepository
import pl.transport.truck.db.repository.PhoneNumberRepository
import pl.transport.truck.db.repository.UserRepository
import pl.transport.truck.specification.RepositorySpecification
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class PsqlJobPhoneRepositoryTest extends RepositorySpecification {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PsqlJobPhoneRepository jobPhoneRepository

    @Autowired
    private JobRepository jobRepository

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    def "test JobPhoneEntity is properly saved"() {
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

        then: "JobPhoneEntity is saved"
        JobPhoneEntity jobPhoneEntity = JobPhoneEntity.builder()
                .jobId(jobId.get())
                .phoneNumberId(phoneNumberId.get())
                .build()
        StepVerifier.create(jobPhoneRepository.save(jobPhoneEntity))
                .expectNextCount(1)
                .verifyComplete()

        cleanup:
        testRepositoryUtils.deleteJobById(jobId.get())
        testRepositoryUtils.deleteUserById(userId.get())
    }
}
