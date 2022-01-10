package pl.transport.truck.db.repository.psql;

import io.r2dbc.spi.Row;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.entity.*;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.JobDetailsRepository;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
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
public class PsqlJobDetailsRepository implements JobDetailsRepository {

    private static final String JOB_PREFIX = "job_";
    private static final String PHONE_NUMBER_PREFIX = "phone_number_";
    private static final String USER_PREFIX = "user_";

    private final DatabaseClient databaseClient;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<JobDetailsEntity> getJobDetails(Long jobId) {

        String sql = queryFactory.create()
                .select(List.of(
                        "j.id as job_id",
                        "j.title as title",
                        "j.description as description",
                        "j.source_address as source_address",
                        "j.destination_address as destination_address",
                        "j.destination_email as destination_email",
                        "j.created_at as job_created_at",
                        "j.updated_at as job_updated_at",
                        "pn.id as phone_number_id",
                        "pn.phone_prefix as phone_prefix",
                        "pn.phone_number as phone_number",
                        "pn.created_at as phone_number_created_at",
                        "pn.updated_at as phone_number_updated_at",
                        "u.id as user_id",
                        "u.username as username",
                        "u.password as password",
                        "u.first_name as first_name",
                        "u.last_name as last_name",
                        "u.address as address",
                        "u.email as email",
                        "u.role as role",
                        "u.created_at as user_created_at",
                        "u.updated_at as user_updated_at"
                ))
                .from(JobEntity.TABLE_NAME, "j")
                .leftJoin("job_phone_number jpn")
                .on("jpn.job_id = j.id")
                .leftJoin("phone_number pn")
                .on("pn.id = jpn.phone_number_id")
                .leftJoin("users u")
                .on("u.id = j.customer_id")
                .where("j.id = :jobId")
                .build();
        return databaseClient.sql(sql)
                .bind("jobId", jobId)
                .map(JobWithPhoneAndCustomer::fromRow)
                .all()
                .collect(DriverWithPhoneNumberCollector.getInstance());
    }

    private static class DriverWithPhoneNumberCollector extends AbstractSetCollector<JobWithPhoneAndCustomer, JobDetailsEntity> {

        public static DriverWithPhoneNumberCollector getInstance() {
            return new DriverWithPhoneNumberCollector();
        }

        @Override
        public Function<Set<JobWithPhoneAndCustomer>, JobDetailsEntity> finisher() {
            return set -> {
                if (set.isEmpty()) {
                    return JobDetailsEntity.builder().build();
                } else {
                    JobWithPhoneAndCustomer jobWithPhoneAndCustomer = List.copyOf(set).get(0);
                    return JobDetailsEntity.builder()
                            .id(jobWithPhoneAndCustomer.getId())
                            .title(jobWithPhoneAndCustomer.getTitle())
                            .description(jobWithPhoneAndCustomer.getDescription())
                            .sourceAddress(jobWithPhoneAndCustomer.getSourceAddress())
                            .destinationAddress(jobWithPhoneAndCustomer.getDestinationAddress())
                            .destinationEmail(jobWithPhoneAndCustomer.getDestinationEmail())
                            .updatedAt(jobWithPhoneAndCustomer.getUpdatedAt())
                            .createdAt(jobWithPhoneAndCustomer.getCreatedAt())
                            .phones(set.stream()
                                    .map(JobWithPhoneAndCustomer::getPhone)
                                    .map(phone -> PhoneNumberEntity.builder()
                                            .id(phone.getId())
                                            .phoneNumber(phone.getPhoneNumber())
                                            .phonePrefix(phone.getPhonePrefix())
                                            .createdAt(phone.getCreatedAt())
                                            .updatedAt(phone.getUpdatedAt())
                                            .build())
                                    .collect(Collectors.toSet()))
                            .customer(jobWithPhoneAndCustomer.getCustomer())
                            .build();
                }
            };
        }
    }

    @Value
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    private static class JobWithPhoneAndCustomer extends BaseEntity {

        String title;
        String description;
        String sourceAddress;
        String destinationAddress;
        String destinationEmail;
        PhoneNumberEntity phone;
        UserEntity customer;

        public static JobWithPhoneAndCustomer fromRow(Row row) {
            return JobWithPhoneAndCustomer.builder()
                    .id(row.get(JOB_PREFIX + BaseEntity.ID, Long.class))
                    .title(row.get(JobEntity.TITLE, String.class))
                    .description(row.get(JobEntity.DESCRIPTION, String.class))
                    .sourceAddress(row.get(JobEntity.SOURCE_ADDRESS, String.class))
                    .destinationAddress(row.get(JobEntity.DESTINATION_ADDRESS, String.class))
                    .destinationEmail(row.get(JobEntity.DESTINATION_EMAIL, String.class))
                    .createdAt(row.get(JOB_PREFIX + BaseEntity.CREATED_AT, ZonedDateTime.class))
                    .updatedAt(row.get(JOB_PREFIX + BaseEntity.UPDATED_AT, ZonedDateTime.class))
                    .phone(PhoneNumberEntity.builder()
                            .id(row.get(PHONE_NUMBER_PREFIX + BaseEntity.ID, Long.class))
                            .phonePrefix(row.get(PhoneNumberEntity.PHONE_PREFIX, String.class))
                            .phoneNumber(row.get(PhoneNumberEntity.PHONE_NUMBER, String.class))
                            .createdAt(row.get(PHONE_NUMBER_PREFIX + BaseEntity.CREATED_AT, ZonedDateTime.class))
                            .updatedAt(row.get(PHONE_NUMBER_PREFIX + BaseEntity.UPDATED_AT, ZonedDateTime.class))
                            .build())
                    .customer(UserEntity.builder()
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
                            .build())
                    .build();
        }
    }
}
