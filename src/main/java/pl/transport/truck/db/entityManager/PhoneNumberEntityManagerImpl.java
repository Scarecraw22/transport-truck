package pl.transport.truck.db.entityManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.transport.truck.common.ex.NotImplementedException;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.repository.PhoneNumberRepository;
import pl.transport.truck.db.utils.EntityManager;
import reactor.core.publisher.Mono;

@Slf4j
@EntityManager
@RequiredArgsConstructor
public class PhoneNumberEntityManagerImpl implements PhoneNumberEntityManager {

    private final PhoneNumberRepository phoneNumberRepository;

    @Override
    public Mono<PhoneNumberEntity> getById(Long id) {
        return phoneNumberRepository.findById(id);
    }

    @Override
    public Mono<PhoneNumberEntity> save(PhoneNumberEntity entity) {
        return phoneNumberRepository.save(entity)
                .doOnNext(phoneNumberEntity -> log.info("Created PhoneNumber: {}", entity));
    }

    @Override
    public Mono<PhoneNumberEntity> update(PhoneNumberEntity entity) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<PhoneNumberEntity> getPhoneByPrefixAndNumber(String prefix, String number) {
        return Mono.fromRunnable(() -> log.info("Trying to find PhoneNumber by prefix: {{}] and number: [{}]", prefix, number))
                .flatMap(nothing -> phoneNumberRepository.getPhoneByPrefixAndNumber(prefix, number))
                .doOnNext(phone -> log.info("Found PhoneNumberEntity: {}", phone))
                .doOnError(error -> log.error("Error while searching PhoneNumber by prefix: {{}] and number: [{}]. Exception", prefix, number, error));

    }
}
