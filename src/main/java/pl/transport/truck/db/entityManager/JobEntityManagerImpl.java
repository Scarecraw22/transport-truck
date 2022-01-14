package pl.transport.truck.db.entityManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.transport.truck.common.ex.NotImplementedException;
import pl.transport.truck.db.entity.JobEntity;
import pl.transport.truck.db.entity.JobPhoneEntity;
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
    private final JobPhoneRepository jobPhoneRepository;

    @Override
    public Mono<JobEntity> getById(Long id) {
        return jobRepository.findById(id)
                .doOnNext(job -> log.info("Found job: {} by id: [{}]", job, id))
                .doOnError(error -> log.error("Error while trying to find job by id: [{}]. Exception: ", id, error));
    }

    @Override
    public Mono<JobEntity> save(JobEntity entity) {
        return Mono.fromRunnable(() -> log.info("Trying to create new Job: {}", entity))
                .flatMap(nothing -> jobRepository.save(entity))
                .doOnNext(job -> log.info("Saved job: {}", job))
                .doOnError(error -> log.error("Error while trying to save new job: {}. Exception: ", entity, error));
    }

    @Override
    public Mono<JobEntity> update(JobEntity entity) {
        throw new NotImplementedException();
    }

    @Override
    public Flux<JobPhoneEntity> addPhonesToJob(Collection<JobPhoneEntity> entities) {
        return Mono.fromRunnable(() -> log.info("Trying to add phones to job by saving entities: {}", entities))
                .flatMapMany(nothing -> jobPhoneRepository.saveAll(entities))
                .doOnNext(jobPhone -> log.info("Linked phone with id: [{}] to job with id: [{}]", jobPhone.getPhoneNumberId(), jobPhone.getJobId()))
                .doOnError(error -> log.error("Error while saving entities: {}. Exception: ", entities, error));
    }

    @Override
    public Mono<JobPhoneEntity> addPhoneToJob(JobPhoneEntity entity) {
        return Mono.fromRunnable(() -> log.info("trying to add phone to job by saving entity: {}", entity))
                .flatMap(nothing -> jobPhoneRepository.save(entity))
                .doOnNext(jobPhone -> log.info("Linked phone with id: [{}] to job with id: [{}]", jobPhone.getPhoneNumberId(), jobPhone.getJobId()))
                .doOnError(error -> log.error("Error while saving entity: {}. Exception: ", entity, error));
    }
}
