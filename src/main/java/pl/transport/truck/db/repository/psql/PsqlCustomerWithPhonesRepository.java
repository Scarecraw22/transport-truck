package pl.transport.truck.db.repository.psql;

import io.r2dbc.spi.Row;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.BaseEntity;
import pl.transport.truck.db.entity.CustomerEntity;
import pl.transport.truck.db.entity.CustomerWithPhonesEntity;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.CustomerWithPhonesRepository;
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
public class PsqlCustomerWithPhonesRepository implements CustomerWithPhonesRepository {

    private static final String CUSTOMER_PREFIX = "customer_";
    private static final String PHONE_NUMBER_PREFIX = "phone_number_";

    private final DatabaseClient databaseClient;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<CustomerWithPhonesEntity> getCustomerWithPhones(Long customerId) {

        String sql = queryFactory.create()
                .select(List.of(
                        "c.id as customer_id",
                        "c.password as password",
                        "c.first_name as first_name",
                        "c.last_name as last_name",
                        "c.address as address",
                        "c.email as email",
                        "c.created_at as customer_created_at",
                        "c.updated_at as customer_updated_at",
                        "pn.id as phone_number_id",
                        "pn.phone_prefix as phone_prefix",
                        "pn.phone_number as phone_number",
                        "pn.created_at as phone_number_created_at",
                        "pn.updated_at as phone_number_updated_at"
                ))
                .from(DbConsts.SCHEMA, CustomerEntity.TABLE_NAME, "c")
                .leftJoin("tt.customer_phone_number cpn")
                .on("cpn.customer_id = c.id")
                .leftJoin("tt.phone_number pn")
                .on("pn.id = cpn.phone_number_id")
                .where("c.id = :customerId")
                .build();
        return databaseClient.sql(sql)
                .bind("customerId", customerId)
                .map(CustomerWithSinglePhone::fromRow)
                .all()
                .collect(CustomerWithPhoneNumberCollector.getInstance());
    }

    private static class CustomerWithPhoneNumberCollector extends AbstractSetCollector<CustomerWithSinglePhone, CustomerWithPhonesEntity> {

        public static CustomerWithPhoneNumberCollector getInstance() {
            return new CustomerWithPhoneNumberCollector();
        }

        @Override
        public Function<Set<CustomerWithSinglePhone>, CustomerWithPhonesEntity> finisher() {
            return set -> {
                if (set.isEmpty()) {
                    return CustomerWithPhonesEntity.builder().build();
                } else {
                    CustomerWithSinglePhone customerWithSinglePhone = List.copyOf(set).get(0);
                    return CustomerWithPhonesEntity.builder()
                            .id(customerWithSinglePhone.getId())
                            .password(customerWithSinglePhone.getPassword())
                            .firstName(customerWithSinglePhone.getFirstName())
                            .lastName(customerWithSinglePhone.getLastName())
                            .address(customerWithSinglePhone.getAddress())
                            .email(customerWithSinglePhone.getEmail())
                            .createdAt(customerWithSinglePhone.getCreatedAt())
                            .updatedAt(customerWithSinglePhone.getUpdatedAt())
                            .phoneNumbers(set.stream()
                                    .map(CustomerWithSinglePhone::getPhone)
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
    private static class CustomerWithSinglePhone extends CustomerEntity {
        PhoneNumberEntity phone;

        public static CustomerWithSinglePhone fromRow(Row row) {
            return CustomerWithSinglePhone.builder()
                    .id(row.get(CUSTOMER_PREFIX + BaseEntity.ID, Long.class))
                    .password(row.get(CustomerEntity.PASSWORD, String.class))
                    .firstName(row.get(CustomerEntity.FIRST_NAME, String.class))
                    .lastName(row.get(CustomerEntity.LAST_NAME, String.class))
                    .address(row.get(CustomerEntity.ADDRESS, String.class))
                    .email(row.get(CustomerEntity.EMAIL, String.class))
                    .createdAt(row.get(CUSTOMER_PREFIX + BaseEntity.CREATED_AT, LocalDateTime.class))
                    .updatedAt(row.get(CUSTOMER_PREFIX + BaseEntity.UPDATED_AT, LocalDateTime.class))
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
