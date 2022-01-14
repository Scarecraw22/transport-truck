package pl.transport.truck.db.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import reactor.core.publisher.Mono;

@Repository
public interface PhoneNumberRepository extends R2dbcRepository<PhoneNumberEntity, Long> {

    @Query("SELECT * FROM PHONE_NUMBER WHERE phone_prefix = :prefix AND phone_number = :number")
    Mono<PhoneNumberEntity> getPhoneByPrefixAndNumber(String prefix, String number);
}
