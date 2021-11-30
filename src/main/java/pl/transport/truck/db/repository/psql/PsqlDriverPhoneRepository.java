package pl.transport.truck.db.repository.psql;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.converter.DriverPhoneReadingConverter;
import pl.transport.truck.db.entity.DriverPhoneEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.DriverPhoneRepository;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
import pl.transport.truck.db.utils.DbConsts;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@ConditionalOnPsqlDb
@RequiredArgsConstructor
public class PsqlDriverPhoneRepository implements DriverPhoneRepository {

    private final DatabaseClient databaseClient;
    private final DriverPhoneReadingConverter driverPhoneReadingConverter;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<DriverPhoneEntity> save(DriverPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .insertInto(DbConsts.SCHEMA, DriverPhoneEntity.TABLE_NAME, List.of(DriverPhoneEntity.DRIVER_ID, DriverPhoneEntity.PHONE_NUMBER_ID))
                        .values(entity.getDriverId(), entity.getPhoneNumberId())
                        .returningAll()
                        .build())
                .map(driverPhoneReadingConverter::convert)
                .one();
    }

    @Override
    public Mono<DriverPhoneEntity> delete(DriverPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .deleteFrom(DbConsts.SCHEMA, DriverPhoneEntity.TABLE_NAME)
                        .where("driver_id = :driverId AND phone_number_id = :phoneNumberId")
                        .returningAll()
                        .build())
                .bind("driverId", entity.getDriverId())
                .bind("phoneNumberId", entity.getPhoneNumberId())
                .map(driverPhoneReadingConverter::convert)
                .one();
    }
}
