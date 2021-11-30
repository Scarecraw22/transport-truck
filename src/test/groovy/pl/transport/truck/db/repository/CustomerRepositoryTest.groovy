package pl.transport.truck.db.repository

import org.springframework.beans.factory.annotation.Autowired
import pl.transport.truck.db.entity.CustomerEntity
import pl.transport.truck.db.repository.CustomerRepository
import pl.transport.truck.specification.RepositorySpecification
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.util.concurrent.atomic.AtomicLong

class CustomerRepositoryTest extends RepositorySpecification {

    @Autowired
    private CustomerRepository customerRepository

    def "test if CustomerEntity is properly saved and retrieved"() {
        given:
        CustomerEntity customer = CustomerEntity.builder()
                .password("password")
                .salt("salt")
                .firstName("f1")
                .lastName("l1")
                .address("a1")
                .email("e1")
                .updatedAt(now)
                .createdAt(now)
                .build()

        when: "Customer is saved"
        Mono<CustomerEntity> monoCustomer = customerRepository.save(customer)

        then: "Get generated id"
        AtomicLong id = new AtomicLong()
        StepVerifier.create(monoCustomer.log())
                .consumeNextWith(c -> {
                    id.set(c.getId())
                })
                .verifyComplete()

        when: "User is retrieved from db"
        Mono<CustomerEntity> customerFromDb = customerRepository.findById(id.get())

        then: "Check if all fields are properly set"
        StepVerifier.create(customerFromDb.log())
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
        StepVerifier.create(customerRepository.deleteById(id.get()).log())
                .expectNextCount(0)
                .verifyComplete()
    }
}
