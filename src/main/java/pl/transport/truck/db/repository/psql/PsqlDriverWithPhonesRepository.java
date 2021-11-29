package pl.transport.truck.db.repository.psql;

import io.r2dbc.spi.Row;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.BaseEntity;
import pl.transport.truck.db.entity.DriverEntity;
import pl.transport.truck.db.entity.DriverWithPhonesEntity;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.DriverWithPhonesRepository;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
import pl.transport.truck.db.utils.DbConsts;
import pl.transport.truck.utils.AbstractSetCollector;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@ConditionalOnPsqlDb
@RequiredArgsConstructor
public class PsqlDriverWithPhonesRepository implements DriverWithPhonesRepository {

    private static final String DRIVER_PREFIX = "driver_";
    private static final String PHONE_NUMBER_PREFIX = "phone_number_";

    private final DatabaseClient databaseClient;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<DriverWithPhonesEntity> getCustomerWithPhones(Long driverId) {

        String sql = queryFactory.create()
                .select(List.of(
                        "d.id as driver_id",
                        "d.password as password",
                        "d.first_name as first_name",
                        "d.last_name as last_name",
                        "d.address as address",
                        "d.email as email",
                        "d.created_at as driver_created_at",
                        "d.updated_at as driver_updated_at",
                        "pn.id as phone_number_id",
                        "pn.phone_prefix as phone_prefix",
                        "pn.phone_number as phone_number",
                        "pn.created_at as phone_number_created_at",
                        "pn.updated_at as phone_number_updated_at"
                ))
                .from(DbConsts.SCHEMA, DriverEntity.TABLE_NAME, "d")
                .leftJoin("tt.driver_phone_number dpn")
                .on("dpn.driver_id = d.id")
                .leftJoin("tt.phone_number pn")
                .on("pn.id = dpn.phone_number_id")
                .where("d.id = :driverId")
                .build();
        return databaseClient.sql(sql)
                .bind("driverId", driverId)
                .map(DriverWithSinglePhone::fromRow)
                .all()
                .collect(DriverWithPhoneNumberCollector.getInstance());
    }

    private static class DriverWithPhoneNumberCollector extends AbstractSetCollector<DriverWithSinglePhone, DriverWithPhonesEntity> {

        public static DriverWithPhoneNumberCollector getInstance() {
            return new DriverWithPhoneNumberCollector();
        }

        @Override
        public Function<Set<DriverWithSinglePhone>, DriverWithPhonesEntity> finisher() {
            return set -> {
                if (set.isEmpty()) {
                    return DriverWithPhonesEntity.builder().build();
                } else {
                    DriverWithSinglePhone customerWithSinglePhone = List.copyOf(set).get(0);
                    return DriverWithPhonesEntity.builder()
                            .id(customerWithSinglePhone.getId())
                            .password(customerWithSinglePhone.getPassword())
                            .firstName(customerWithSinglePhone.getFirstName())
                            .lastName(customerWithSinglePhone.getLastName())
                            .address(customerWithSinglePhone.getAddress())
                            .email(customerWithSinglePhone.getEmail())
                            .createdAt(customerWithSinglePhone.getCreatedAt())
                            .updatedAt(customerWithSinglePhone.getUpdatedAt())
                            .phoneNumbers(set.stream()
                                    .map(DriverWithSinglePhone::getPhone)
                                    .map(phone -> PhoneNumberEntity.builder()
                                            .id(phone.getId())
                                            .phoneNumber(phone.getPhoneNumber())
                                            .phonePrefix(phone.getPhonePrefix())
                                            .createdAt(phone.getCreatedAt())
                                            .updatedAt(phone.getUpdatedAt())
                                            .build())
                                    .collect(Collectors.toSet()))
                            .build();
                }
            };
        }
    }

    @Value
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    private static class DriverWithSinglePhone extends DriverEntity {
        PhoneNumberEntity phone;

        public static DriverWithSinglePhone fromRow(Row row) {
            return DriverWithSinglePhone.builder()
                    .id(row.get(DRIVER_PREFIX + BaseEntity.ID, Long.class))
                    .password(row.get(DriverEntity.PASSWORD, String.class))
                    .firstName(row.get(DriverEntity.FIRST_NAME, String.class))
                    .lastName(row.get(DriverEntity.LAST_NAME, String.class))
                    .address(row.get(DriverEntity.ADDRESS, String.class))
                    .email(row.get(DriverEntity.EMAIL, String.class))
                    .createdAt(row.get(DRIVER_PREFIX + BaseEntity.CREATED_AT, LocalDateTime.class))
                    .updatedAt(row.get(DRIVER_PREFIX + BaseEntity.UPDATED_AT, LocalDateTime.class))
                    .phone(PhoneNumberEntity.builder()
                            .id(row.get(PHONE_NUMBER_PREFIX + BaseEntity.ID, Long.class))
                            .phonePrefix(row.get(PhoneNumberEntity.PHONE_PREFIX, String.class))
                            .phoneNumber(row.get(PhoneNumberEntity.PHONE_NUMBER, String.class))
                            .createdAt(row.get(PHONE_NUMBER_PREFIX + BaseEntity.CREATED_AT, LocalDateTime.class))
                            .updatedAt(row.get(PHONE_NUMBER_PREFIX + BaseEntity.UPDATED_AT, LocalDateTime.class))
                            .build())
                    .build();
        }
    }
}
