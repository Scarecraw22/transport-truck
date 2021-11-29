package pl.transport.truck.db

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.datetime.utils.DateTimeConsts
import pl.transport.truck.db.entity.DriverEntity
import pl.transport.truck.db.repository.DriverRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicLong

class DriverRepositoryTest extends DbSpecification {

    @Autowired
    private DriverRepository driverRepository

    def "test if DriverEntity is properly saved and retrieved"() {
        given:
        LocalDateTime now = LocalDateTime.now(DateTimeConsts.EUROPE_WARSAW_ZONE)
        DriverEntity driver = DriverEntity.builder()
                .password("password")
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

        when: "User is retrieved from db"
        Mono<DriverEntity> driverFromDb = driverRepository.findById(id.get())

        then: "Check if all fields are properly set"
        StepVerifier.create(driverFromDb.log())
                .assertNext(c -> {
                    assert c.getId() == id.get()
                    assert c.getPassword() == "password"
                    assert c.getFirstName() == "f1"
                    assert c.getAddress() == "a1"
                    assert c.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS).isEqual(now.truncatedTo(ChronoUnit.SECONDS))
                    assert c.getCreatedAt().truncatedTo(ChronoUnit.SECONDS).isEqual(now.truncatedTo(ChronoUnit.SECONDS))
                })
    }
}
