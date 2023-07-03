package dev.nisalb.hubwork.api;

import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.api.payload.ReqeustInformation;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Tag(name = "jobs", description = "The job API.")
public interface JobApi {

    @Operation(
            summary = "find all jobs",
            description = "Find all jobs in the service",
            tags = "jobs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Job.class)))
            )
    })
    @GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<Set<Object>> findAllJobs(
            @Parameter(description = "optional user id to filter by the job owner") @RequestParam(value = "ownerId", required = false) Optional<Long> ownerId,
            @Parameter(description = "optional job state to filter by job status") @RequestParam(value = "status", required = false) Optional<JobState> state,
            @Parameter(description = "optional user id to filter by the job worker") @RequestParam(value = "workerId", required = false) Optional<Long> workerId
    );

    @Operation(
            summary = "create a job",
            description = "Post a new job to the service",
            tags = "jobs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "job creation failed due to an error in the request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "job creation failed due to server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping(value = "/jobs", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<Object> createJob(
            @Parameter(description = "information of the job to be created") @RequestBody JobPayload payload
    );

    @Operation(
            summary = "find a job",
            description = "Find a job by ID",
            tags = "jobs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @GetMapping(value = "/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<Object> findJob(
            @Parameter(description = "job id to be found") @PathVariable("id") Long id
    );

    @Operation(
            summary = "delete a job",
            description = "Delete a job if it was not accepted or rejrected. Cancel otherwise",
            tags = "jobs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @DeleteMapping(value = "/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> deleteJob(
            @Parameter(description = "job id to be deleted") @PathVariable("id") Long id
    );

    @Operation(
            summary = "update a job",
            description = "Updates an existing job",
            tags = "jobs"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Job.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "job modification failed due to an error in the request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "job modification failed due to server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping(value = "/jobs/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> updateJob(
            @Parameter(description = "job id to be updated") @PathVariable("id") Long id,
            @Parameter(description = "new job information") @RequestBody JobPayload payload
    );

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
    @PostMapping(value = "/jobs/{id}/requests", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> makeRequest(
            @Parameter(description = "id of the job which the request is made for") @PathVariable("id") Long id,
            @Parameter(description = "request information") @RequestBody ReqeustInformation payload
    );

    @Operation(
            summary = "find a job request",
            description = "Find a job request by ID",
            tags = "jobs"
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
    @GetMapping(value = "/jobs/{jobId}/requests/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<Object> findRequest(
            @Parameter(description = "job id of the request") @PathVariable("jobId") Long jobId,
            @Parameter(description = "id of the request") @PathVariable("reqId") Long reqId
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
    @GetMapping(value = "/jobs/{id}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Set<Object>> getAllRequestsForAJob(
            @Parameter(description = "id of the job") @PathVariable("id") Long id,
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
    @DeleteMapping(value = "/jobs/{jobId}/requests/{reqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> cancelRequest(
            @Parameter(description = "id of the job") @PathVariable("jobId") Long jobId,
            @Parameter(description = "id of the request") @PathVariable("reqId") Long reqId
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
    @PatchMapping(value = "/jobs/{jobId}/requests/{reqId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> updateRequest(
            @Parameter(description = "id of the job") @PathVariable("jobId") Long jobId,
            @Parameter(description = "id of the request") @PathVariable("reqId") Long reqId,
            @Parameter(description = "new request state. only the state can be updated") @RequestBody ReqeustInformation payload
    );
}
