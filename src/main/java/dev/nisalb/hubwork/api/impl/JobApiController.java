package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.JobApi;
import dev.nisalb.hubwork.api.payload.JobInformation;
import dev.nisalb.hubwork.api.payload.ReqeustInformation;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.RequestState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobApiController implements JobApi {

    @Override
    public ResponseEntity<List<Object>> findAllJobs(Long ownerId, JobState state, Long id) {
        return ResponseEntity.ok(List.of("findAllJobs"));
    }

    @Override
    public ResponseEntity<Object> createJob(JobInformation payload) {
        return ResponseEntity.ok("createJob");
    }

    @Override
    public ResponseEntity<Object> findJob(Long id) {
        return ResponseEntity.ok("findJob:" + id);
    }

    @Override
    public ResponseEntity<Object> deleteJob(Long id) {
        return ResponseEntity.ok("deleteJob:" + id);
    }

    @Override
    public ResponseEntity<Object> updateJob(Long id, JobInformation payload) {
        return ResponseEntity.ok("updateJob:" + id);
    }

    @Override
    public ResponseEntity<Object> makeRequest(Long id, ReqeustInformation payload) {
        return ResponseEntity.ok("makeRequest:" + id);
    }

    @Override
    public ResponseEntity<Object> findRequest(Long jobId, Long reqId) {
        return ResponseEntity.ok("findRequest:" + jobId + ":" + reqId);
    }

    @Override
    public ResponseEntity<List<Object>> getAllRequestsForAJob(Long id, RequestState state) {
        return ResponseEntity.ok(List.of("getAllRequstsForAJob:" + id));
    }

    @Override
    public ResponseEntity<Object> cancelRequest(Long jobId, Long reqId) {
        return ResponseEntity.ok("cancelRequest:" + jobId + ":" + reqId);
    }

    @Override
    public ResponseEntity<Object> updateRequest(Long jobId, Long reqId, ReqeustInformation payload) {
        return ResponseEntity.ok("updateRequest:" + jobId + ":" + reqId);
    }
}
