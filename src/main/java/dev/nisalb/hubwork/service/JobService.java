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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Setter(onMethod = @__({@Autowired}))
    private JobRepository jobRepository;

    @Setter(onMethod = @__({@Autowired}))
    private UserRepository userRepository;

    @Setter(onMethod = @__({@Autowired}))
    private MailService mailService;

    public Set<Job> findAllJobs() {
        var jobs = jobRepository.findAll();
        return asSet(jobs);
    }

    public Set<Job> searchJobsBy(
            Optional<Long> ownerId,
            Optional<JobState> state,
            Optional<Long> workerId
    ) {
        var found = jobRepository.searchBy(
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
        job.setState(JobState.PENDING);
        job.setDueDate(payload.getDueDate());

        // owner must exist and be a CUSTOMER to continue

        Optional<User> givenUser = userRepository.findById(payload.getOwnerId());
        if (givenUser.isEmpty()) {
            return Either.left(ApiError.noSuchUser(payload.getOwnerId()));
        }
        var owner = givenUser.get();

        if (!owner.getRole().equals(UserRole.CUSTOMER)) {
            return Either.left(ApiError.invalidUserRole(owner.getRole(), UserRole.CUSTOMER));
        }

        // good to go

        job.setOwner(owner);

        Job saved;
        try {
            saved = jobRepository.save(job);
        } catch (Exception ex) {
            logger.warn("Exception while saving the job", ex);
            return Either.left(ApiError.internalServerError());
        }

        mailService.sendMail(owner.getEmail(), "HubWork: Job Created:" + job.getTitle(), List.of(
                "Hi " + owner.getFirstName(),
                "You job for '" + job.getTitle() + "' is now posted in HubWork.",
                "You can now request a worker to take the job."
        ));

        return Either.right(saved);
    }

    public Optional<Job> findJob(Long id) {
        return jobRepository.findById(id);
    }

    public Either<ApiError, Job> updateJob(Long id, JobPayload payload) {
        Optional<Job> givenJob = jobRepository.findById(id);
        if (givenJob.isEmpty()) {
            return Either.left(ApiError.noSuchJob(id));
        }

        var job = givenJob.get();

        job.setTitle(payload.getTitle());
        job.setDescription(payload.getDescription());
        job.setState(payload.getState());
        job.setPaymentMethods(payload.getPaymentMethods());
        job.setPrice(payload.getPrice());
        job.setCurrency(payload.getCurrency());
        job.setDueDate(payload.getDueDate());

        try {
            job = jobRepository.save(job);
        } catch (Exception ex) {
            logger.warn("Exception while updating the job", ex);
            return Either.left(ApiError.internalServerError());
        }

        mailService.sendMail(job.getOwner().getEmail(), "HubWork: Job Updated: " + job.getTitle(), List.of(
                "Hi " + job.getOwner().getFirstName(),
                "Your job titled '" + job.getTitle() + "' has been updated."
        ));

        return Either.right(job);
    }

    public Either<ApiError, Job> deleteJob(Long id) {
        Optional<Job> givenJob = jobRepository.findById(id);
        if (givenJob.isEmpty()) {
            return Either.left(ApiError.noSuchJob(id));
        }

        var job = givenJob.get();
        jobRepository.deleteById(id);

        return Either.right(job);
    }

    private <T> Set<T> asSet(Iterable<T> elems) {
        return Sets.newHashSet(elems);
    }
}
