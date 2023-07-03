package dev.nisalb.hubwork.service;

import com.google.common.collect.Sets;
import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.User;
import dev.nisalb.hubwork.model.UserRole;
import dev.nisalb.hubwork.service.repo.JobRepository;
import dev.nisalb.hubwork.service.repo.UserRepository;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Setter(onMethod = @__({@Autowired}))
    private JobRepository jobRepository;

    @Setter(onMethod = @__({@Autowired}))
    private UserRepository userRepository;

    public Set<Job> findAllJobs() {
        var jobs = jobRepository.findAll();
        return asSet(jobs);
    }

    public Set<Job> searchJobsBy(
            Optional<Long> ownerId,
            Optional<JobState> state,
            Optional<Long> workerId
    ) {
        var found = jobRepository.searchJobsBy(
                ownerId.orElse(null),
                state.orElse(null),
                workerId.orElse(null)
        );
        return asSet(found);
    }

    public Either<ApiError, Job> createJob(JobPayload payload, @NonNull Job job) {
        job.setTitle(payload.getTitle());
        job.setDescription(payload.getDescription());
        job.setCurrency(payload.getCurrency());
        job.setPrice(payload.getPrice());
        job.setPaymentMethods(payload.getPaymentMethods());

        // owner must exist and be a CUSTOMER to continue

        Optional<User> givenUser = userRepository.findById(payload.getOwnerId());
        if (givenUser.isEmpty()) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_ID")
                            .description("No such user for id: " + payload.getOwnerId())
                            .build()
            );
        }
        var owner = givenUser.get();

        if (!owner.getRole().equals(UserRole.CUSTOMER)) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_USER")
                            .description("User must be a customer to create a job")
                            .build()
            );
        }

        // good to go

        job.setOwner(owner);

        Job saved;
        try {
            saved = jobRepository.save(job);
        } catch (Exception ex) {
            logger.warn("Exception while saving the job", ex);
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR)
                            .title("UNKNOWN_SERVER_ERROR")
                            .description("Unknown error occurred while saving the Job")
                            .build()
            );
        }

        return Either.right(saved);
    }

    public Optional<Job> findJob(Long id) {
        return jobRepository.findById(id);
    }

    public Either<ApiError, Job> updateJob(Long id, JobPayload payload) {
        Optional<Job> givenJob = jobRepository.findById(id);
        if (givenJob.isEmpty()) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_ID")
                            .description("No such job for id: " + id)
                            .build()
            );
        }

        var job = givenJob.get();

        job.setTitle(payload.getTitle());
        job.setDescription(payload.getDescription());
        job.setState(payload.getState());
        job.setPaymentMethods(payload.getPaymentMethods());
        job.setPrice(payload.getPrice());
        job.setCurrency(payload.getCurrency());
        job.setDueDate(payload.getDueDate());

        Job updated;
        try {
            updated = jobRepository.save(job);
        } catch (Exception ex) {
            logger.warn("Exception while updating the job", ex);
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR)
                            .title("UNKNOWN_ERROR")
                            .description("Unknown server error occurred while updating the job")
                            .build()
            );
        }

        return Either.right(updated);
    }

    public Either<ApiError, Job> deleteJob(Long id) {
        Optional<Job> givenJob = jobRepository.findById(id);
        if (givenJob.isEmpty()) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_ID")
                            .description("No such job for id: " + id)
                            .build()
            );
        }

        var job = givenJob.get();
        jobRepository.deleteById(id);

        return Either.right(job);
    }

    private <T> Set<T> asSet(Iterable<T> elems) {
        return Sets.newHashSet(elems);
    }
}
