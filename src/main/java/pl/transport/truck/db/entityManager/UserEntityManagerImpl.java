package pl.transport.truck.db.entityManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.transport.truck.common.ex.NotImplementedException;
import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.db.entity.UserPhoneEntity;
import pl.transport.truck.db.repository.UserDetailsRepository;
import pl.transport.truck.db.repository.UserPhoneRepository;
import pl.transport.truck.db.repository.UserRepository;
import pl.transport.truck.db.utils.EntityManager;
import reactor.core.publisher.Mono;

@Slf4j
@EntityManager
@RequiredArgsConstructor
public class UserEntityManagerImpl implements UserEntityManager {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserPhoneRepository userPhoneRepository;

    @Override
    public Mono<UserEntity> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<UserEntity> save(UserEntity entity) {
        log.info("Saving user with username: {}", entity.getUsername());
        return userRepository.save(entity);
    }

    @Override
    public Mono<UserEntity> update(UserEntity entity) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<UserDetailsEntity> getUserDetails(Long customerId) {
        return userDetailsRepository.getUserDetails(customerId).log();
    }

    @Override
    public Mono<UserEntity> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Mono<UserPhoneEntity> addPhoneToUser(Long userId, Long phoneNumberId) {
        return userPhoneRepository.save(UserPhoneEntity.builder()
                .userId(userId)
                .phoneNumberId(phoneNumberId)
                .build());
    }
}
