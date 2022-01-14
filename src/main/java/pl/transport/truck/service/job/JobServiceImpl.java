package pl.transport.truck.service.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.transport.truck.converter.JobModelConverter;
import pl.transport.truck.db.entity.JobPhoneEntity;
import pl.transport.truck.db.entity.PhoneNumberEntity;
import pl.transport.truck.db.entityManager.JobEntityManager;
import pl.transport.truck.db.entityManager.PhoneNumberEntityManager;
import pl.transport.truck.rest.model.job.CreateJobRequest;
import pl.transport.truck.rest.model.job.CreateJobResponse;
import pl.transport.truck.service.common.EntityTimestampService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobEntityManager jobEntityManager;
    private final PhoneNumberEntityManager phoneNumberEntityManager;
    private final EntityTimestampService entityTimestampService;
    private final JobModelConverter jobModelConverter;

    @Override
    public Mono<CreateJobResponse> createJob(CreateJobRequest request) {
        return Mono.just(request)
                .map(jobModelConverter::convertToJobEntity)
                .flatMap(jobEntityManager::save)
                .flatMap(newJob -> {
                    return Flux.fromIterable(request.getJobPhones())
                            .flatMap(jobPhone -> {
                                return phoneNumberEntityManager.getPhoneByPrefixAndNumber(jobPhone.getPrefix(), jobPhone.getNumber())
                                        .switchIfEmpty(phoneNumberEntityManager.save(convertToPhoneNumberEntity(jobPhone)));
                            })
                            .flatMap(phoneNumberEntity -> jobEntityManager.addPhoneToJob(JobPhoneEntity.builder()
                                    .jobId(newJob.getId())
                                    .phoneNumberId(phoneNumberEntity.getId())
                                    .build()))
                            .then()
                            .map(nothing -> newJob.getId());
                })
                .map(CreateJobResponse::fromJobId);
    }

    private PhoneNumberEntity convertToPhoneNumberEntity(CreateJobRequest.PhoneNumber phone) {
        PhoneNumberEntity entity = PhoneNumberEntity.builder()
                .phonePrefix(phone.getPrefix())
                .phoneNumber(phone.getNumber())
                .build();
        entityTimestampService.setupDatesForNewEntity(entity);

        return entity;
    }
}
