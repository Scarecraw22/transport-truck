package pl.transport.truck.db.entityManager;

import lombok.RequiredArgsConstructor;
import pl.transport.truck.common.ex.NotImplementedException;
import pl.transport.truck.db.entity.CustomerDetailsEntity;
import pl.transport.truck.db.entity.CustomerEntity;
import pl.transport.truck.db.repository.CustomerDetailsRepository;
import pl.transport.truck.db.repository.CustomerRepository;
import pl.transport.truck.db.utils.EntityManager;
import reactor.core.publisher.Mono;

@EntityManager
@RequiredArgsConstructor
public class CustomerEntityManagerImpl implements CustomerEntityManager {

    private final CustomerRepository customerRepository;
    private final CustomerDetailsRepository customerDetailsRepository;

    @Override
    public Mono<CustomerEntity> getById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Mono<CustomerEntity> save(CustomerEntity entity) {
        return customerRepository.save(entity);
    }

    @Override
    public Mono<CustomerEntity> update(CustomerEntity entity) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<CustomerDetailsEntity> getCustomerDetails(Long customerId) {
        return customerDetailsRepository.getCustomerDetails(customerId);
    }
}
