package pl.transport.truck.db.repository.psql

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.PhoneNumberEntity
import pl.transport.truck.db.entity.UserDetailsEntity
import pl.transport.truck.db.entity.UserEntity
import pl.transport.truck.db.entity.UserPhoneEntity
import pl.transport.truck.db.repository.PhoneNumberRepository
import pl.transport.truck.db.repository.UserDetailsRepository
import pl.transport.truck.db.repository.UserPhoneRepository
import pl.transport.truck.db.repository.UserRepository
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class PsqlUserDetailsRepositoryTest extends RepositorySpecification {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    @Autowired
    private UserPhoneRepository userPhoneRepository

    @Autowired
    private UserDetailsRepository userDetailsRepository

    def "test if User with PhoneNumber is properly retrieved"() {
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

        PhoneNumberEntity phoneNumberEntity = PhoneNumberEntity.builder()
                .phonePrefix("48")
                .phoneNumber("111111111")
                .createdAt(now)
                .updatedAt(now)
                .build()

        when: "user and PhoneNumber is saved"
        AtomicLong userId = new AtomicLong()
        StepVerifier.create(userRepository.save(user).log())
                .consumeNextWith(c -> userId.set(c.getId()))
                .verifyComplete()

        AtomicLong phoneNumberId = new AtomicLong()
        StepVerifier.create(phoneNumberRepository.save(phoneNumberEntity).log())
                .consumeNextWith(pn -> phoneNumberId.set(pn.getId()))
                .verifyComplete()

        UserPhoneEntity userPhoneEntity = UserPhoneEntity.builder()
                .userId(userId.get())
                .phoneNumberId(phoneNumberId.get())
                .build()
        StepVerifier.create(userPhoneRepository.save(userPhoneEntity).log())
                .expectNextCount(1)
                .verifyComplete()

        and: "User with phone number is retrieved"
        Mono<UserDetailsEntity> userWithPhones = userDetailsRepository.getUserDetails(userId.get())

        then:
        StepVerifier.create(userWithPhones.log())
                .assertNext(uwp -> {
                    assert uwp.getId() == userId.get()
                    assert uwp.getUsername() == "u1"
                    assert uwp.getFirstName() == "f1"
                    assert uwp.getLastName() == "l1"
                    assert uwp.getEmail() == "e1"
                    assert uwp.getRole() == "r1"
                    assert uwp.getAddress() == "a1"
                    assert uwp.getCreatedAt() != null
                    assert uwp.getUpdatedAt() != null
                    assert !uwp.getPhoneNumbers().isEmpty()
                    uwp.getPhoneNumbers().forEach(pn -> {
                        assert pn.getId() != null
                        assert pn.getPhonePrefix() == "48"
                        assert pn.getPhoneNumber() == "111111111"
                        assert pn.getCreatedAt() != null
                        assert pn.getUpdatedAt() != null
                    })
                })
                .verifyComplete()

        cleanup:
        testRepositoryUtils.deleteUserById(userId.get())
    }
}
