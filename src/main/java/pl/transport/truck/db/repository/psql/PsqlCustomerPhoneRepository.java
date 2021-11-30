package pl.transport.truck.db.repository.psql;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.converter.CustomerPhoneReadingConverter;
import pl.transport.truck.db.entity.CustomerPhoneEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
import pl.transport.truck.db.utils.DbConsts;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@ConditionalOnPsqlDb
@RequiredArgsConstructor
public class PsqlCustomerPhoneRepository implements pl.transport.truck.db.repository.PsqlCustomerPhoneRepository {

    private final DatabaseClient databaseClient;
    private final CustomerPhoneReadingConverter customerPhoneReadingConverter;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<CustomerPhoneEntity> save(CustomerPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .insertInto(DbConsts.SCHEMA, CustomerPhoneEntity.TABLE_NAME, List.of("customer_id", "phone_number_id"))
                        .values(entity.getCustomerId(), entity.getPhoneNumberId())
                        .returningAll()
                        .build())
                .map(customerPhoneReadingConverter::convert)
                .one();
    }

    @Override
    public Mono<CustomerPhoneEntity> delete(CustomerPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .deleteFrom(DbConsts.SCHEMA, CustomerPhoneEntity.TABLE_NAME)
                        .where("customer_id = :customerId AND phone_number_id = :phoneNumberId")
                        .returningAll()
                        .build())
                .bind("customerId", entity.getCustomerId())
                .bind("phoneNumberId", entity.getPhoneNumberId())
                .map(customerPhoneReadingConverter::convert)
                .one();
    }
}
