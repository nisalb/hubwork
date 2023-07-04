package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.JobApi;
import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.service.JobService;
import dev.nisalb.hubwork.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

import static dev.nisalb.hubwork.api.payload.util.Responses.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class JobApiController implements JobApi {


    private final JobService jobService;

    private final ValidationService validationService;

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
        var validation = validationService.validate(payload, "invalid payload to create a job");
        if (validation.isError()) {
            return badRequest(validation);
        }

        var job = new Job();
        var result = jobService.createJob(payload, job);

        if (result.isLeft()) {
            return error(result.getLeft());
        }

        return ok(result.get());
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
            return error(result.getLeft());
        }

        return ok(result.get());
    }

    @Override
    public ResponseEntity<Object> updateJob(Long id, JobPayload payload) {
        var validation = validationService.validate(payload, "invalid payload to update a job");
        if (validation.isError()) {
            return badRequest(validation);
        }

        var result = jobService.updateJob(id, payload);

        if (result.isLeft()) {
            return error(result.getLeft());
        }

        return ResponseEntity.ok(result.get());
    }
}
