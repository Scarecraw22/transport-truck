package pl.transport.truck.db.repository.psql;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.converter.UserPhoneReadingConverter;
import pl.transport.truck.db.entity.UserPhoneEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.UserPhoneRepository;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@ConditionalOnPsqlDb
@RequiredArgsConstructor
public class PsqlUserPhoneRepository implements UserPhoneRepository {

    private final DatabaseClient databaseClient;
    private final UserPhoneReadingConverter userPhoneReadingConverter;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<UserPhoneEntity> save(UserPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .insertInto(UserPhoneEntity.TABLE_NAME, List.of(UserPhoneEntity.USER_ID, UserPhoneEntity.PHONE_NUMBER_ID))
                        .values(entity.getUserId(), entity.getPhoneNumberId())
                        .returningAll()
                        .build())
                .map(userPhoneReadingConverter::convert)
                .one();
    }

    @Override
    public Mono<UserPhoneEntity> delete(UserPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .deleteFrom(UserPhoneEntity.TABLE_NAME)
                        .where("user_id = :userId AND phone_number_id = :phoneNumberId")
                        .returningAll()
                        .build())
                .bind("userId", entity.getUserId())
                .bind("phoneNumberId", entity.getPhoneNumberId())
                .map(userPhoneReadingConverter::convert)
                .one();
    }

    @Override
    public Flux<UserPhoneEntity> getByUserId(Long userId) {
        return databaseClient.sql(queryFactory.create()
                        .select(UserPhoneEntity.USER_ID, UserPhoneEntity.PHONE_NUMBER_ID)
                        .from(UserPhoneEntity.TABLE_NAME)
                        .where("user_id = :userId")
                        .build())
                .bind("userId", userId)
                .map(userPhoneReadingConverter::convert)
                .all();
    }
}
