package pl.transport.truck.db.repository

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.PhoneNumberEntity
import pl.transport.truck.db.entity.UserEntity
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class PhoneNumberRepositoryTest extends RepositorySpecification {

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    def "test if phoneNumber is properly saved and retrieved by prefix and number"() {
        given:
        PhoneNumberEntity phoneNumberEntity = PhoneNumberEntity.builder()
                .phonePrefix("48")
                .phoneNumber("111111111")
                .createdAt(now)
                .build()

        when: "user is saved"
        Mono<PhoneNumberEntity> mono = phoneNumberRepository.save(phoneNumberEntity)

        then: "Get generated id"
        AtomicLong id = new AtomicLong()
        StepVerifier.create(mono.log())
                .consumeNextWith(c -> {
                    id.set(c.getId())
                })
                .verifyComplete()

        when: "PhoneNumber is retrieved from db by prefix and number"
        Mono<PhoneNumberEntity> phoneFromDb = phoneNumberRepository.getPhoneByPrefixAndNumber("48", "111111111")

        then: "Check if all fields are properly set"
        StepVerifier.create(phoneFromDb.log())
                .assertNext(c -> {
                    assert c.getId() == id.get()
                    assert c.getPhonePrefix() == "48"
                    assert c.getPhoneNumber() == "111111111"
                    assert c.getCreatedAt().isEqual(now)
                })

        cleanup:
        StepVerifier.create(phoneNumberRepository.deleteById(id.get()).log())
                .expectNextCount(0)
                .verifyComplete()
    }
}
