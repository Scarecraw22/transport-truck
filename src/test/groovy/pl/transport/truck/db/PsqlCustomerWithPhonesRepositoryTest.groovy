package pl.transport.truck.db

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.datetime.utils.DateTimeConsts
import pl.transport.truck.db.entity.CustomerEntity
import pl.transport.truck.db.entity.CustomerPhoneEntity
import pl.transport.truck.db.entity.CustomerWithPhonesEntity
import pl.transport.truck.db.entity.PhoneNumberEntity
import pl.transport.truck.db.repository.CustomerPhoneRepository
import pl.transport.truck.db.repository.CustomerRepository
import pl.transport.truck.db.repository.CustomerWithPhonesRepository
import pl.transport.truck.db.repository.PhoneNumberRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

class PsqlCustomerWithPhonesRepositoryTest extends DbSpecification {

    @Autowired
    private CustomerRepository customerRepository

    @Autowired
    private PhoneNumberRepository phoneNumberRepository

    @Autowired
    private CustomerPhoneRepository customerPhoneRepository

    @Autowired
    private CustomerWithPhonesRepository customerWithPhoneNumberRepository

    def "test if Customer with PhoneNumber is properly retrieved"() {
        given:
        LocalDateTime now = LocalDateTime.now(DateTimeConsts.EUROPE_WARSAW_ZONE)
        CustomerEntity customer = CustomerEntity.builder()
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
                .phoneNumber("111111111")
                .createdAt(now)
                .updatedAt(now)
                .build()

        when: "Customer and PhoneNumber is saved"
        AtomicLong customerId = new AtomicLong()
        StepVerifier.create(customerRepository.save(customer).log())
                .consumeNextWith(c -> customerId.set(c.getId()))
                .verifyComplete()

        AtomicLong phoneNumberId = new AtomicLong()
        StepVerifier.create(phoneNumberRepository.save(phoneNumberEntity).log())
                .consumeNextWith(pn -> phoneNumberId.set(pn.getId()))
                .verifyComplete()

        CustomerPhoneEntity customerPhoneEntity = CustomerPhoneEntity.builder()
                .customerId(customerId.get())
                .phoneNumberId(phoneNumberId.get())
                .build()
        StepVerifier.create(customerPhoneRepository.save(customerPhoneEntity).log())
                .expectNextCount(1)
                .verifyComplete()

        and: "Customer with phone number is retrieved"
        Mono<CustomerWithPhonesEntity> customerWithPhones = customerWithPhoneNumberRepository.getCustomerWithPhones(customerId.get())

        then:
        StepVerifier.create(customerWithPhones.log())
                .assertNext(cwp -> {
                    assert cwp.getId() == customerId.get()
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
                        assert pn.getPhoneNumber() == "111111111"
                        assert pn.getCreatedAt() != null
                        assert pn.getUpdatedAt() != null
                    })
                })
                .verifyComplete()
    }
}
