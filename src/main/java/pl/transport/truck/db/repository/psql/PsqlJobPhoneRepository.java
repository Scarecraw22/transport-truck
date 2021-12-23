package pl.transport.truck.db.repository.psql;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import pl.transport.truck.db.converter.JobPhoneReadingConverter;
import pl.transport.truck.db.entity.JobPhoneEntity;
import pl.transport.truck.db.query.StringQueryBuilderFactory;
import pl.transport.truck.db.repository.JobPhoneRepository;
import pl.transport.truck.db.utils.ConditionalOnPsqlDb;
import pl.transport.truck.db.utils.DbConsts;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@ConditionalOnPsqlDb
@RequiredArgsConstructor
public class PsqlJobPhoneRepository implements JobPhoneRepository {

    private final DatabaseClient databaseClient;
    private final JobPhoneReadingConverter jobPhoneReadingConverter;
    private final StringQueryBuilderFactory queryFactory;

    @Override
    public Mono<JobPhoneEntity> save(JobPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .insertInto(JobPhoneEntity.TABLE_NAME, List.of(JobPhoneEntity.JOB_ID, JobPhoneEntity.PHONE_NUMBER_ID))
                        .values(entity.getJobId(), entity.getPhoneNumberId())
                        .returningAll()
                        .build())
                .map(jobPhoneReadingConverter::convert)
                .one();
    }

    @Override
    public Mono<JobPhoneEntity> delete(JobPhoneEntity entity) {
        return databaseClient.sql(queryFactory.create()
                        .deleteFrom(JobPhoneEntity.TABLE_NAME)
                        .where("job_id = :jobId AND phone_number_id = :phoneNumberId")
                        .returningAll()
                        .build())
                .bind("jobId", entity.getJobId())
                .bind("phoneNumberId", entity.getPhoneNumberId())
                .map(jobPhoneReadingConverter::convert)
                .one();
    }
}
