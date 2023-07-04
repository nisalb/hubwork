package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.JobApi;
import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.api.payload.RequestPayload;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.Request;
import dev.nisalb.hubwork.model.RequestState;
import dev.nisalb.hubwork.service.JobService;
import dev.nisalb.hubwork.service.RequestService;
import jakarta.validation.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class JobApiController implements JobApi {

    @Setter(onMethod = @__({@Autowired}))
    private JobService jobService;

    @Setter(onMethod = @__({@Autowired}))
    private RequestService requestService;

    @Setter(onMethod = @__({@Autowired}))
    private Validator validator;

    @Override
    public ResponseEntity<Set<Job>> findAllJobs(
            Optional<Long> ownerId,
            Optional<JobState> state,
            Optional<Long> workerId
    ) {
        Set<Job> result;

        if (ownerId.isEmpty() && state.isEmpty() && workerId.isEmpty()) {
            result = jobService.findAllJobs();
        } else {
            result = jobService.searchJobsBy(ownerId, state, workerId);
        }

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Object> createJob(JobPayload payload) {
        var validation = validate(payload, "invalid payload to create a job");
        if (validation.isError()) {
            return ResponseEntity.badRequest().body(validation);
        }

        var job = new Job();
        var result = jobService.createJob(payload, job);

        if (result.isLeft()) {
            ApiError error = result.getLeft();
            return buildErrorResponse(error);
        }

        return ResponseEntity.ok(result.get());
    }

    @Override
    public ResponseEntity<Job> findJob(Long id) {
        var theJob = jobService.findJob(id);

        return theJob
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Object> deleteJob(Long id) {
        var result = jobService.deleteJob(id);

        if (result.isLeft()) {
            ApiError error = result.getLeft();
            return buildErrorResponse(error);
        }

        return ResponseEntity.ok(result.get());
    }

    @Override
    public ResponseEntity<Object> updateJob(Long id, JobPayload payload) {
        var validation = validate(payload, "invalid payload to update a job");
        if (validation.isError()) {
            return ResponseEntity.badRequest().body(validation);
        }

        var result = jobService.updateJob(id, payload);

        if (result.isLeft()) {
            ApiError error = result.getLeft();
            return buildErrorResponse(error);
        }

        return ResponseEntity.ok(result.get());
    }

    @Override
    public ResponseEntity<Object> makeRequest(Long id, RequestPayload payload) {
        var validation = validate(payload, "invalid payload to create a request");
        if (validation.isError()) {
            return ResponseEntity.badRequest().body(validation);
        }

        var request = new Request();
        var result = requestService.createRequest(payload, request);

        if (result.isLeft()) {
            ApiError error = result.getLeft();
            return buildErrorResponse(error);
        }

        return ResponseEntity.ok(result.get());
    }

    @Override
    public ResponseEntity<Object> findRequest(Long jobId, Long reqId) {
        // TODO
        return ResponseEntity.ok("findRequest:" + jobId + ":" + reqId);
    }

    @Override
    public ResponseEntity<Set<Object>> getAllRequestsForAJob(Long id, Optional<RequestState> state) {
        // TODO
        return ResponseEntity.ok(Set.of("getAllRequstsForAJob:" + id));
    }

    @Override
    public ResponseEntity<Object> cancelRequest(Long jobId, Long reqId) {
        // TODO
        return ResponseEntity.ok("cancelRequest:" + jobId + ":" + reqId);
    }

    @Override
    public ResponseEntity<Object> updateRequest(Long jobId, Long reqId, RequestPayload payload) {
        // TODO
        return ResponseEntity.ok("updateRequest:" + jobId + ":" + reqId);
    }

    private <T> ApiError validate(T payload, String message) {
        var violations = validator.validate(payload);

        if (violations.isEmpty())
            return ApiError.ok();

        Set<String> errors = violations.stream()
                .map(violation -> {
                    var msg = violation.getMessage();
                    var value = violation.getInvalidValue();
                    return msg + " (given = [" + value + "])";
                })
                .collect(Collectors.toSet());

        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_PAYLOAD")
                .description(message)
                .property("violations", errors)
                .build();
    }

    private ResponseEntity<Object> buildErrorResponse(ApiError error) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(error.getCode())) {
            return ResponseEntity.internalServerError().body(error);
        }

        return ResponseEntity.badRequest().body(error);
    }
}
