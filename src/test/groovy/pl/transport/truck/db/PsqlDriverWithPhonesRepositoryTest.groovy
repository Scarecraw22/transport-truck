package pl.transport.truck.db

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.datetime.utils.DateTimeConsts
import pl.transport.truck.db.entity.DriverEntity
import pl.transport.truck.db.entity.DriverPhoneEntity
import pl.transport.truck.db.entity.DriverWithPhonesEntity
import pl.transport.truck.db.entity.PhoneNumberEntity
import pl.transport.truck.db.repository.DriverPhoneRepository
import pl.transport.truck.db.repository.DriverRepository
import pl.transport.truck.db.repository.DriverWithPhonesRepository
import pl.transport.truck.db.repository.PhoneNumberRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

class PsqlDriverWithPhonesRepositoryTest extends DbSpecification {

    @Autowired
    private DriverRepository driverRepository

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    @Autowired
    private DriverPhoneRepository driverPhoneRepository

    @Autowired
    private DriverWithPhonesRepository driverWithPhoneNumberRepository

    def "test if Customer with PhoneNumber is properly retrieved"() {
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

        PhoneNumberEntity phoneNumberEntity = PhoneNumberEntity.builder()
                .phonePrefix("48")
                .phoneNumber("222222222")
                .createdAt(now)
                .updatedAt(now)
                .build()

        when: "Driver and PhoneNumber is saved"
        AtomicLong driverId = new AtomicLong()
        StepVerifier.create(driverRepository.save(driver).log())
                .consumeNextWith(c -> driverId.set(c.getId()))
                .verifyComplete()

        AtomicLong phoneNumberId = new AtomicLong()
        StepVerifier.create(phoneNumberRepository.save(phoneNumberEntity).log())
                .consumeNextWith(pn -> phoneNumberId.set(pn.getId()))
                .verifyComplete()

        DriverPhoneEntity driverPhoneEntity = DriverPhoneEntity.builder()
                .driverId(driverId.get())
                .phoneNumberId(phoneNumberId.get())
                .build()
        StepVerifier.create(driverPhoneRepository.save(driverPhoneEntity).log())
                .expectNextCount(1)
                .verifyComplete()

        and: "Driver with phone number is retrieved"
        Mono<DriverWithPhonesEntity> driverWithPhones = driverWithPhoneNumberRepository.getCustomerWithPhones(driverId.get())

        then:
        StepVerifier.create(driverWithPhones.log())
                .assertNext(cwp -> {
                    assert cwp.getId() == driverId.get()
                    assert cwp.getFirstName() == "f1"
                    assert cwp.getLastName() == "l1"
                    assert cwp.getEmail() == "e1"
                    assert cwp.getAddress() == "a1"
                    assert cwp.getCreatedAt() != null
                    assert cwp.getUpdatedAt() != null
                    assert !cwp.getPhoneNumbers().isEmpty()
                    cwp.getPhoneNumbers().forEach(pn -> {
                        assert pn.getId() != null
                        assert pn.getPhonePrefix() == "48"
                        assert pn.getPhoneNumber() == "222222222"
                        assert pn.getCreatedAt() != null
                        assert pn.getUpdatedAt() != null
                    })
                })
                .verifyComplete()
    }
}
