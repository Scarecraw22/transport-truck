package pl.transport.truck.db.repository.psql

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.DriverDetailsEntity
import pl.transport.truck.db.entity.DriverEntity
import pl.transport.truck.db.entity.DriverPhoneEntity
import pl.transport.truck.db.entity.PhoneNumberEntity
import pl.transport.truck.db.repository.DriverRepository
import pl.transport.truck.db.repository.PhoneNumberRepository
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class PsqlDriverDetailsRepositoryTest extends RepositorySpecification {

    @Autowired
    private DriverRepository driverRepository

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    @Autowired
    private PsqlDriverPhoneRepository driverPhoneRepository

    @Autowired
    private PsqlDriverDetailsRepository driverDetailsRepository

    def "test if Customer with PhoneNumber is properly retrieved"() {
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
        Mono<DriverDetailsEntity> driverWithPhones = driverDetailsRepository.getCustomerDetails(driverId.get())

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

        cleanup:
        StepVerifier.create(driverPhoneRepository.delete(driverPhoneEntity).log())
                .expectNextCount(1)
                .verifyComplete()
        StepVerifier.create(phoneNumberRepository.deleteById(phoneNumberId.get()).log())
                .expectNextCount(0)
                .verifyComplete()
        StepVerifier.create(driverRepository.deleteById(driverId.get()).log())
                .expectNextCount(0)
                .verifyComplete()
    }
}
