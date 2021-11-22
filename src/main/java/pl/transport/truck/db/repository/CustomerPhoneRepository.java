package pl.transport.truck.db.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.converter.CustomerPhoneReadingConverter;
import pl.transport.truck.db.entity.CustomerPhoneEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.utils.DbConsts;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerPhoneRepository {

    private final DatabaseClient databaseClient;
    private final CustomerPhoneReadingConverter customerPhoneReadingConverter;
    private final StringQueryBuilderFactory queryFactory;

    public Mono<CustomerPhoneEntity> save(CustomerPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .insertInto(DbConsts.SCHEMA, CustomerPhoneEntity.TABLE_NAME, List.of("customer_id", "phone_number_id"))
                        .values(entity.getCustomerId(), entity.getPhoneNumberId())
                        .returningAll()
                        .build())
                .map(customerPhoneReadingConverter::convert)
                .one();
    }

}
