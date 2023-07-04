package dev.nisalb.hubwork.api;

import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.CreateRequestPayload;
import dev.nisalb.hubwork.api.payload.UpdateRequestPayload;
import dev.nisalb.hubwork.model.Request;
import dev.nisalb.hubwork.model.RequestState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Tag(name = "requests", description = "The request API.")
@RequestMapping("/jobs/{jobId}")
public interface RequestApi {
    @Operation(
            summary = "make a job request",
            description = "Make a request for a worker for a job",
            tags = "requests"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Request.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "request failed due to a malformed payload",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "job not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "conflicting operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "request failed due to a system error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping(value = "/requests", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> makeRequest(
            @Parameter(description = "id of the job which the request is made for") @PathVariable("jobId") Long jobIid,
            @Parameter(description = "request information") @RequestBody CreateRequestPayload payload
    );

    @Operation(
            summary = "find a job request",
            description = "Find a job request by ID",
            tags = "requests"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Request.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @GetMapping(value = "/requests/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Request> findRequest(
            @Parameter(description = "job id of the request") @PathVariable("jobId") Long jobId,
            @Parameter(description = "id of the request") @PathVariable("reqId") UUID reqId
    );

    @Operation(
            summary = "get all requests for a job",
            description = "Get all requests for a job, optionally filtered by status",
            tags = "requests"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Request.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "job not found"
            ),
    })
    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Set<Request>> getAllRequestsForAJob(
            @Parameter(description = "id of the job") @PathVariable("jobId") Long jobId,
            @Parameter(description = "optional request status") @RequestParam(value = "status", required = false) Optional<RequestState> state
    );

    @Operation(
            summary = "delete a job request",
            description = "Delete a job request if it is not accepted or rejected.",
            tags = "requests"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Request.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "job or request is not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "conflicting operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping(value = "/requests/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> cancelRequest(
            @Parameter(description = "id of the job") @PathVariable("jobId") Long jobId,
            @Parameter(description = "id of the request") @PathVariable("reqId") UUID reqId
    );

    @Operation(
            summary = "update the request state",
            description = "Accept, reject or cancel a job request state by updating the state.",
            tags = "requests"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Request.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "job or request is not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "conflicting operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PatchMapping(value = "/requests/{reqId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> updateRequest(
            @Parameter(description = "id of the job") @PathVariable("jobId") Long jobId,
            @Parameter(description = "id of the request") @PathVariable("reqId") UUID reqId,
            @Parameter(description = "new request state. only the state can be updated") @RequestBody UpdateRequestPayload payload
    );
}
