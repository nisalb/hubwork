package dev.nisalb.hubwork.service;

import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.RequestPayload;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Setter(onMethod = @__({@Autowired}))
    private RequestRepository requestRepository;

    @Setter(onMethod = @__({@Autowired}))
    private UserRepository userRepository;

    @Setter(onMethod = @__({@Autowired}))
    private JobRepository jobRepository;

    public Either<ApiError, Request> createRequest(RequestPayload payload, Request request) {

        var givenJob = jobRepository.findById(payload.getJobID());
        if (givenJob.isEmpty()) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_ID")
                            .description("No such job for id: " + payload.getJobID())
                            .build()
            );
        }

        var job = givenJob.get();
        if (!job.getState().equals(JobState.PENDING)) {
            String errorMessage = switch (job.getState()) {
                case GRANTED -> "Job is already granted to a worker. ";
                case COMPLETED -> "Job is completed. ";
                case CANCELLED -> "Job is cancelled. ";
                default -> "";
            };
            errorMessage += "Cannot make any more requests.";

            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_JOB")
                            .description(errorMessage)
                            .build()
            );
        }

        var givenWorker = userRepository.findById(payload.getWorkerId());
        if (givenWorker.isEmpty()) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_ID")
                            .description("No such user for id: " + payload.getWorkerId())
                            .build()
            );
        }

        var worker = givenWorker.get();
        if (!worker.getRole().equals(UserRole.WORKER)) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("INVALID_USER")
                            .description("specified user is not a worker")
                            .build()
            );
        }

        var requestId = new RequestId(UUID.randomUUID(), job, worker);

        var existingRequest = requestRepository.findById(requestId);
        if (existingRequest.isPresent()) {
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.BAD_REQUEST)
                            .title("DUPLICATE_REQUEST")
                            .description("This request is already created.")
                            .build()
            );
        }

        request.setId(requestId);
        request.setState(RequestState.PENDING);

        Request saved;
        try {
            saved = requestRepository.save(request);
        } catch (Exception ex) {
            logger.warn("Exception while creating the request", ex);
            return Either.left(
                    ApiError.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR)
                            .title("UNKNOWN_ERROR")
                            .description("Unknown server error occurred while creating the request")
                            .build()
            );
        }

        return Either.right(saved);
    }
}
