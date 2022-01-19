package pl.transport.truck.db.entityManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.transport.truck.common.ex.NotImplementedException;
import pl.transport.truck.db.entity.JobDetailsEntity;
import pl.transport.truck.db.entity.JobEntity;
import pl.transport.truck.db.entity.JobPhoneEntity;
import pl.transport.truck.db.repository.JobDetailsRepository;
import pl.transport.truck.db.repository.JobPhoneRepository;
import pl.transport.truck.db.repository.JobRepository;
import pl.transport.truck.db.utils.EntityManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Slf4j
@EntityManager
@RequiredArgsConstructor
public class JobEntityManagerImpl implements JobEntityManager {

    private final JobRepository jobRepository;
    private final JobDetailsRepository jobDetailsRepository;
    private final JobPhoneRepository jobPhoneRepository;

    @Override
    public Mono<JobEntity> getById(Long id) {
        return jobRepository.findById(id)
                .doOnNext(job -> log.info("Found job: {} by id: [{}]", job, id))
                .doOnError(error -> log.error("Error while trying to find job by id: [{}]. Exception: ", id, error));
    }

    @Override
    public Mono<JobEntity> save(JobEntity entity) {
        log.info("Trying to create new Job: {}", entity);
        return jobRepository.save(entity)
                .doOnSuccess(job -> log.info("Saved job: {}", job))
                .doOnError(error -> log.error("Error while trying to save new job: {}. Exception: ", entity, error));
    }

    @Override
    public Mono<JobEntity> update(JobEntity entity) {
        throw new NotImplementedException();
    }

    @Override
    public Flux<JobPhoneEntity> addPhonesToJob(Collection<JobPhoneEntity> entities) {
        log.info("Trying to add phones to job by saving entities: {}", entities);
        return jobPhoneRepository.saveAll(entities)
                .doOnNext(jobPhone -> log.info("Linked phone with id: [{}] to job with id: [{}]", jobPhone.getPhoneNumberId(), jobPhone.getJobId()))
                .doOnError(error -> log.error("Error while saving entities: {}. Exception: ", entities, error));
    }

    @Override
    public Mono<JobPhoneEntity> addPhoneToJob(JobPhoneEntity entity) {
        log.info("Trying to add phone to job by saving entity: {}", entity);
        return jobPhoneRepository.save(entity)
                .doOnNext(jobPhone -> log.info("Linked phone with id: [{}] to job with id: [{}]", jobPhone.getPhoneNumberId(), jobPhone.getJobId()))
                .doOnError(error -> log.error("Error while saving entity: {}. Exception: ", entity, error));
    }

    @Override
    public Mono<JobDetailsEntity> getJobDetails(Long jobId) {
        log.info("Trying to get JobDetails with id: {}", jobId);
        return jobDetailsRepository.getJobDetails(jobId)
                .doOnSuccess(jobDetails -> log.info("Successfully found JobDetails with id: {}", jobDetails.getId()))
                .doOnError(error -> log.error("Error while trying to get JobDetails with id: {}. Exception: ", jobId, error));
    }
}
