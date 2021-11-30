package pl.transport.truck.db.repository

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.DriverEntity
import pl.transport.truck.db.repository.DriverRepository
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class DriverRepositoryTest extends RepositorySpecification {

    @Autowired
    private DriverRepository driverRepository

    def "test if DriverEntity is properly saved and retrieved"() {
        given:
        DriverEntity driver = DriverEntity.builder()
                .password("password")
                .salt("salt")
                .firstName("f1")
                .lastName("l1")
                .address("a1")
                .email("e1")
                .updatedAt(now)
                .createdAt(now)
                .build()

        when: "Driver is saved"
        Mono<DriverEntity> monoDriver = driverRepository.save(driver)

        then: "Get generated id"
        AtomicLong id = new AtomicLong()
        StepVerifier.create(monoDriver.log())
                .consumeNextWith(c -> {
                    id.set(c.getId())
                })
                .verifyComplete()

        when: "Driver is retrieved from db"
        Mono<DriverEntity> driverFromDb = driverRepository.findById(id.get())

        then: "Check if all fields are properly set"
        StepVerifier.create(driverFromDb.log())
                .assertNext(c -> {
                    assert c.getId() == id.get()
                    assert c.getPassword() == "password"
                    assert c.getSalt() == "salt"
                    assert c.getFirstName() == "f1"
                    assert c.getAddress() == "a1"
                    assert c.getUpdatedAt().isEqual(now)
                    assert c.getCreatedAt().isEqual(now)
                })

        cleanup:
        StepVerifier.create(driverRepository.deleteById(id.get()).log())
                .expectNextCount(0)
                .verifyComplete()
    }
}
