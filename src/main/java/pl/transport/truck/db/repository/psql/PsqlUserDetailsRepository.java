package pl.transport.truck.db.repository.psql;

import io.r2dbc.spi.Row;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.BaseEntity;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.entity.UserDetailsEntity;
import pl.transport.truck.db.entity.UserEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.UserDetailsRepository;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
import pl.transport.truck.db.utils.DbConsts;
import pl.transport.truck.utils.AbstractSetCollector;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@ConditionalOnPsqlDb
@RequiredArgsConstructor
public class PsqlUserDetailsRepository implements UserDetailsRepository {

    private static final String USER_PREFIX = "user_";
    private static final String PHONE_NUMBER_PREFIX = "phone_number_";

    private final DatabaseClient databaseClient;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<UserDetailsEntity> getUserDetails(Long userId) {

        String sql = queryFactory.create()
                .select(List.of(
                        "u.id as user_id",
                        "u.username as username",
                        "u.password as password",
                        "u.first_name as first_name",
                        "u.last_name as last_name",
                        "u.address as address",
                        "u.email as email",
                        "u.role as role",
                        "u.created_at as user_created_at",
                        "u.updated_at as user_updated_at",
                        "pn.id as phone_number_id",
                        "pn.phone_prefix as phone_prefix",
                        "pn.phone_number as phone_number",
                        "pn.created_at as phone_number_created_at",
                        "pn.updated_at as phone_number_updated_at"
                ))
                .from(UserEntity.TABLE_NAME, "u")
                .leftJoin("user_phone_number upn")
                .on("upn.user_id = u.id")
                .leftJoin("phone_number pn")
                .on("pn.id = upn.phone_number_id")
                .where("u.id = :userId")
                .build();
        return databaseClient.sql(sql)
                .bind("userId", userId)
                .map(UserWithSinglePhone::fromRow)
                .all()
                .collect(UserWithPhoneNumberCollector.getInstance());
    }

    private static class UserWithPhoneNumberCollector extends AbstractSetCollector<UserWithSinglePhone, UserDetailsEntity> {

        public static UserWithPhoneNumberCollector getInstance() {
            return new UserWithPhoneNumberCollector();
        }

        @Override
        public Function<Set<UserWithSinglePhone>, UserDetailsEntity> finisher() {
            return set -> {
                if (set.isEmpty()) {
                    return UserDetailsEntity.builder().build();
                } else {
                    UserWithSinglePhone customerWithSinglePhone = List.copyOf(set).get(0);
                    return UserDetailsEntity.builder()
                            .id(customerWithSinglePhone.getId())
                            .username(customerWithSinglePhone.getUsername())
                            .firstName(customerWithSinglePhone.getFirstName())
                            .lastName(customerWithSinglePhone.getLastName())
                            .address(customerWithSinglePhone.getAddress())
                            .email(customerWithSinglePhone.getEmail())
                            .role(customerWithSinglePhone.getRole())
                            .createdAt(customerWithSinglePhone.getCreatedAt())
                            .updatedAt(customerWithSinglePhone.getUpdatedAt())
                            .phoneNumbers(set.stream()
                                    .map(UserWithSinglePhone::getPhone)
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
    private static class UserWithSinglePhone extends UserEntity {
        PhoneNumberEntity phone;

        public static UserWithSinglePhone fromRow(Row row) {
            return UserWithSinglePhone.builder()
                    .id(row.get(USER_PREFIX + BaseEntity.ID, Long.class))
                    .username(row.get(UserEntity.USERNAME, String.class))
                    .password(row.get(UserEntity.PASSWORD, String.class))
                    .firstName(row.get(UserEntity.FIRST_NAME, String.class))
                    .lastName(row.get(UserEntity.LAST_NAME, String.class))
                    .address(row.get(UserEntity.ADDRESS, String.class))
                    .email(row.get(UserEntity.EMAIL, String.class))
                    .role(row.get(UserEntity.ROLE, String.class))
                    .createdAt(row.get(USER_PREFIX + BaseEntity.CREATED_AT, ZonedDateTime.class))
                    .updatedAt(row.get(USER_PREFIX + BaseEntity.UPDATED_AT, ZonedDateTime.class))
                    .phone(PhoneNumberEntity.builder()
                            .id(row.get(PHONE_NUMBER_PREFIX + BaseEntity.ID, Long.class))
                            .phonePrefix(row.get(PhoneNumberEntity.PHONE_PREFIX, String.class))
                            .phoneNumber(row.get(PhoneNumberEntity.PHONE_NUMBER, String.class))
                            .createdAt(row.get(PHONE_NUMBER_PREFIX + BaseEntity.CREATED_AT, ZonedDateTime.class))
                            .updatedAt(row.get(PHONE_NUMBER_PREFIX + BaseEntity.UPDATED_AT, ZonedDateTime.class))
                            .build())
                    .build();
        }
    }
}
