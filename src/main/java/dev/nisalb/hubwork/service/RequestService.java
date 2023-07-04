package dev.nisalb.hubwork.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.CreateRequestPayload;
import dev.nisalb.hubwork.api.payload.UpdateRequestPayload;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.Request;
import dev.nisalb.hubwork.model.RequestState;
import dev.nisalb.hubwork.model.UserRole;
import dev.nisalb.hubwork.model.key.RequestId;
import dev.nisalb.hubwork.service.repo.JobRepository;
import dev.nisalb.hubwork.service.repo.RequestRepository;
import dev.nisalb.hubwork.service.repo.UserRepository;
import io.vavr.control.Either;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static dev.nisalb.hubwork.model.RequestState.*;
import static java.util.Map.entry;

@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Setter(onMethod = @__({@Autowired}))
    private RequestRepository requestRepository;

    @Setter(onMethod = @__({@Autowired}))
    private UserRepository userRepository;

    @Setter(onMethod = @__({@Autowired}))
    private JobRepository jobRepository;

    private final static Map<RequestState, Set<RequestState>> validJobStateTransitions = Map.ofEntries(
            entry(PENDING, Set.of(ACCEPTED, REJECTED, CANCELLED)),
            entry(ACCEPTED, Set.of(CANCELLED)),
            entry(REJECTED, Set.of(ACCEPTED, CANCELLED)),
            entry(CANCELLED, Set.of())
    );

    public Either<ApiError, Request> createRequest(Long jobId, CreateRequestPayload payload, Request request) {

        var givenJob = jobRepository.findById(jobId);
        if (givenJob.isEmpty()) {
            return Either.left(ApiError.noSuchJob(jobId));
        }

        var job = givenJob.get();
        if (!job.getState().equals(JobState.PENDING)) {
            return Either.left(ApiError.invalidJobState(job.getState()));
        }

        var givenWorker = userRepository.findById(payload.getWorkerId());
        if (givenWorker.isEmpty()) {
            return Either.left(ApiError.noSuchUser(payload.getWorkerId()));
        }

        var user = givenWorker.get();
        if (!user.getRole().equals(UserRole.WORKER)) {
            return Either.left(ApiError.invalidUserRole(user.getRole(), UserRole.WORKER));
        }

        var requestId = new RequestId(UUID.randomUUID(), job, user);

        var existingRequest = requestRepository.findById(requestId);
        if (existingRequest.isPresent()) {
            return Either.left(ApiError.duplicateRequest(requestId.getId()));
        }

        request.setId(requestId);
        request.setState(RequestState.PENDING);

        Request saved;
        try {
            saved = requestRepository.save(request);
        } catch (Exception ex) {
            logger.warn("Exception while creating the request", ex);
            return Either.left(ApiError.internalServerError());
        }

        return Either.right(saved);
    }

    public Either<ApiError, Request> updateRequest(Long jobId, UUID reqId, UpdateRequestPayload payload) {
        var givenJob = jobRepository.findById(jobId);
        if (givenJob.isEmpty()) {
            return Either.left(ApiError.noSuchJob(jobId));
        }

        var givenRequest = requestRepository.findByUniqueId(reqId);
        if (givenRequest.isEmpty()) {
            return Either.left(ApiError.noSuchRequest(reqId));
        }

        var request = givenRequest.get();
        if (!isValidTransition(request.getState(), payload.getState())) {
            return Either.left(ApiError.invalidRequestStateTransition(request.getState(), payload.getState()));
        }

        if (payload.getState().equals(ACCEPTED)) {
            var job = givenJob.get();
            job.setWorker(request.getWorker());
        }

        request.setState(payload.getState());

        try {
            request = requestRepository.save(request);
        } catch (Exception ex) {
            logger.warn("Unexpected error occurred while updating the job state.");
            return Either.left(ApiError.internalServerError());
        }

        return Either.right(request);
    }

    public Optional<Request> findRequest(Long jobId, UUID reqId) {
        var result = requestRepository.searchBy(jobId, reqId, null);

        // there is either one or no requests for this criteria
        var request = Lists.newArrayList(result).get(0);

        return Optional.ofNullable(request);
    }

    public Set<Request> searchRequestsBy(
            Optional<Long> jobId,
            Optional<UUID> reqId,
            Optional<RequestState> state

    ) {
        var found = requestRepository.searchBy(
                jobId.orElse(null),
                reqId.orElse(null),
                state.map(RequestState::name).orElse(null)
        );
        return Sets.newHashSet(found);
    }

    private boolean isValidTransition(RequestState from, RequestState to) {
        return validJobStateTransitions.containsKey(from) &&
                validJobStateTransitions.get(from).contains(to);
    }
}
