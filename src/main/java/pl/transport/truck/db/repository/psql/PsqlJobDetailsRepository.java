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
public class PsqlJobDetailsRepository implements JobDetailsRepository {

    private static final String JOB_PREFIX = "job_";
    private static final String PHONE_NUMBER_PREFIX = "phone_number_";
    private static final String CUSTOMER_PREFIX = "customer_";

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
                        "c.id as customer_id",
                        "c.password as password",
                        "c.first_name as first_name",
                        "c.last_name as last_name",
                        "c.address as address",
                        "c.email as email",
                        "c.created_at as customer_created_at",
                        "c.updated_at as customer_updated_at"
                ))
                .from(DbConsts.SCHEMA, JobEntity.TABLE_NAME, "j")
                .leftJoin("tt.job_phone_number jpn")
                .on("jpn.job_id = j.id")
                .leftJoin("tt.phone_number pn")
                .on("pn.id = jpn.phone_number_id")
                .leftJoin("tt.customer c")
                .on("c.id = j.customer_id")
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
        CustomerEntity customer;

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
                    .customer(CustomerEntity.builder()
                            .id(row.get(CUSTOMER_PREFIX + BaseEntity.ID, Long.class))
                            .password(row.get(CustomerEntity.PASSWORD, String.class))
                            .firstName(row.get(CustomerEntity.FIRST_NAME, String.class))
                            .lastName(row.get(CustomerEntity.LAST_NAME, String.class))
                            .address(row.get(CustomerEntity.ADDRESS, String.class))
                            .email(row.get(CustomerEntity.EMAIL, String.class))
                            .createdAt(row.get(CUSTOMER_PREFIX + BaseEntity.CREATED_AT, ZonedDateTime.class))
                            .updatedAt(row.get(CUSTOMER_PREFIX + BaseEntity.UPDATED_AT, ZonedDateTime.class))
                            .build())
                    .build();
        }
    }
}
