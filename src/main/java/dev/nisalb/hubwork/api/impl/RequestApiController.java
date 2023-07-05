package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.RequestApi;
import dev.nisalb.hubwork.api.payload.CreateRequestPayload;
import dev.nisalb.hubwork.api.payload.UpdateRequestPayload;
import dev.nisalb.hubwork.model.Request;
import dev.nisalb.hubwork.model.RequestState;
import dev.nisalb.hubwork.service.RequestService;
import dev.nisalb.hubwork.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static dev.nisalb.hubwork.api.payload.util.Responses.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class RequestApiController implements RequestApi {

    private final RequestService requestService;

    private final ValidationService validationService;

    @Override
    public ResponseEntity<Object> makeRequest(Long jobId, CreateRequestPayload payload) {
        var validation = validationService.validate(payload, "invalid payload to create a request");
        if (validation.isError()) {
            return badRequest(validation);
        }

        var request = new Request();
        var result = requestService.createRequest(jobId, payload, request);

        if (result.isLeft()) {
            return error(result.getLeft());
        }

        var saved = result.get();
        return created("/jobs/" + jobId + "/requests/" + saved.getId(), saved);
    }

    @Override
    public ResponseEntity<Request> findRequest(Long jobId, UUID reqId) {
        var theRequest = requestService.findRequest(jobId, reqId);

        return theRequest
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Set<Request>> getAllRequestsForAJob(Long jobId, Optional<RequestState> state) {
        var theRequests = requestService.searchRequestsBy(
                Optional.ofNullable(jobId),
                Optional.empty(),
                state);

        return ok(theRequests);
    }

    @Override
    public ResponseEntity<Object> cancelRequest(Long jobId, UUID reqId) {
        UpdateRequestPayload payload = new UpdateRequestPayload();
        payload.setState(RequestState.CANCELLED);

        return updateRequest(jobId, reqId, payload);
    }

    @Override
    public ResponseEntity<Object> updateRequest(Long jobId, UUID reqId, UpdateRequestPayload payload) {
        var validation = validationService.validate(payload, "invalid payload to update the job request");
        if (validation.isError()) {
            return badRequest(validation);
        }

        var result = requestService.updateRequest(jobId, reqId, payload);
        if (result.isLeft()) {
            return error(result.getLeft());
        }

        return ok(result.get());
    }
}
