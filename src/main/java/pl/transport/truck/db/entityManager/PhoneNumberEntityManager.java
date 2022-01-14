package pl.transport.truck.db.entityManager;

import pl.transport.truck.db.entity.PhoneNumberEntity;
import reactor.core.publisher.Mono;

public interface PhoneNumberEntityManager extends BaseEntityManager<PhoneNumberEntity> {

    Mono<PhoneNumberEntity> getPhoneByPrefixAndNumber(String prefix, String number);
}
