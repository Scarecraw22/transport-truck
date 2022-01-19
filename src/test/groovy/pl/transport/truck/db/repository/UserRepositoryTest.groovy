package pl.transport.truck.db.repository

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.UserEntity
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class UserRepositoryTest extends RepositorySpecification {

    @Autowired
    private UserRepository userRepository

    def "test if userEntity is properly saved and retrieved"() {
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
        Mono<UserEntity> monoUser = userRepository.save(user)

        then: "Get generated id"
        AtomicLong id = new AtomicLong()
        StepVerifier.create(monoUser.log())
                .consumeNextWith(c -> {
                    id.set(c.getId())
                })
                .verifyComplete()

        when: "User is retrieved from db"
        Mono<UserEntity> userFromDb = userRepository.findById(id.get())

        then: "Check if all fields are properly set"
        StepVerifier.create(userFromDb.log())
                .assertNext(c -> {
                    assert c.getId() == id.get()
                    assert c.getUsername() == "u1"
                    assert c.getPassword() == "password"
                    assert c.getSalt() == "salt"
                    assert c.getFirstName() == "f1"
                    assert c.getAddress() == "a1"
                    assert c.getEmail() == "e1"
                    assert c.getRole() == "r1"
                    assert c.getUpdatedAt().isEqual(now)
                    assert c.getCreatedAt().isEqual(now)
                })

        cleanup:
        testRepositoryUtils.deleteUserById(id.get())
    }
}
