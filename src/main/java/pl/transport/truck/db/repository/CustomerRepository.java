package pl.transport.truck.db.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends R2dbcRepository<CustomerEntity, Long> {

}
