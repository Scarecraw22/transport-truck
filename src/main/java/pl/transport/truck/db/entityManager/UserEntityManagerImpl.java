package pl.transport.truck.db.entityManager;

import lombok.RequiredArgsConstructor;
import pl.transport.truck.common.ex.NotImplementedException;
import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.db.repository.UserDetailsRepository;
import pl.transport.truck.db.repository.UserRepository;
import pl.transport.truck.db.utils.EntityManager;
import reactor.core.publisher.Mono;

@EntityManager
@RequiredArgsConstructor
public class UserEntityManagerImpl implements UserEntityManager {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    public Mono<UserEntity> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<UserEntity> save(UserEntity entity) {
        return userRepository.save(entity);
    }

    @Override
    public Mono<UserEntity> update(UserEntity entity) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<UserDetailsEntity> getCustomerDetails(Long customerId) {
        return userDetailsRepository.getUserDetails(customerId);
    }

    @Override
    public Mono<UserEntity> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
