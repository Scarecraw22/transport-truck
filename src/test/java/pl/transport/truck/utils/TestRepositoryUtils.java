package pl.transport.truck.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.transport.truck.db.entity.JobPhoneEntity;
import pl.transport.truck.db.entity.UserPhoneEntity;
import pl.transport.truck.db.repository.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestRepositoryUtils {

    private final UserRepository userRepository;
    private final UserPhoneRepository userPhoneRepository;
    private final JobPhoneRepository jobPhoneRepository;
    private final PhoneNumberRepository phoneNumberRepository;
    private final JobRepository jobRepository;

    public void deleteUserById(Long userId) {
        List<Long> phonesToDelete = unlinkPhonesFromUser(userId);
        deletePhoneNumbers(phonesToDelete);

        log.info("Trying to delete User with id: {}", userId);

        StepVerifier.create(userRepository.deleteById(userId).log())
                .verifyComplete();

        log.info("Successfully deleted User with id: {}", userId);
    }

    public List<Long> unlinkPhonesFromUser(Long userId) {
        log.info("Trying to unlink all Phones linked with User with id: {}", userId);
        try {
            List<Long> phoneIds = userPhoneRepository.getByUserId(userId)
                    .flatMap(userPhoneRepository::delete)
                    .map(UserPhoneEntity::getPhoneNumberId)
                    .collectList()
                    .toFuture()
                    .get();

            log.info("Successfully unlinked Phones previously linked with User with id: {}", userId);
            log.info("Returning Phone ids: {}", phoneIds);

            return phoneIds;
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while trying to unlink Phones for User with id: {}. Exception: ", userId, e);
            throw new RuntimeException(e);
        }
    }

    public List<Long> unlinkPhonesFromJob(Long jobId) {
        log.info("Trying to unlink all Phones linked with Job with id: {}", jobId);

        try {
            List<Long> phoneIds = jobPhoneRepository.getByJobId(jobId)
                    .flatMap(jobPhoneRepository::delete)
                    .map(JobPhoneEntity::getPhoneNumberId)
                    .collectList()
                    .toFuture()
                    .get();

            log.info("Successfully unlinked Phones previously linked with Job with id: {}", jobId);
            log.info("Returning Phone ids: {}", phoneIds);

            return phoneIds;
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while trying to unlink Phones for Job with id: {}. Exception: ", jobId, e);
            throw new RuntimeException(e);
        }
    }

    public void deleteJobById(Long jobId) {
        List<Long> phonesToDelete = unlinkPhonesFromJob(jobId);
        deletePhoneNumbers(phonesToDelete);

        log.info("Trying to delete Job with id: {}", jobId);

        StepVerifier.create(jobRepository.deleteById(jobId).log())
                .verifyComplete();

        log.info("Successfully deleted Job with id: {}", jobId);
    }

    public void deletePhoneNumberById(Long phoneNumberId) {
        deletePhoneNumbers(List.of(phoneNumberId));
    }

    public void deletePhoneNumbers(List<Long> phoneNumberIdsToDelete) {
        log.info("Trying to delete PhoneNumbers with ids: {}", phoneNumberIdsToDelete);
        Flux<Void> flux = Flux.fromIterable(phoneNumberIdsToDelete)
                .flatMap(phoneNumberRepository::deleteById)
                .log();

        StepVerifier.create(flux)
                .verifyComplete();

        log.info("Successfully deleted phones with ids: {}", phoneNumberIdsToDelete);
    }
}
