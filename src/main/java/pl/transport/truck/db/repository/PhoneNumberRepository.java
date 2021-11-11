package pl.transport.truck.db.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.PhoneNumberEntity;

@Repository
public interface PhoneNumberRepository extends ReactiveCrudRepository<PhoneNumberEntity, Long> {
}
