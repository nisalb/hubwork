package dev.nisalb.hubwork.api;

import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
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

@Tag(name = "jobs", description = "The jobs API.")
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
    ResponseEntity<Set<Job>> findAllJobs(
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
    ResponseEntity<Job> findJob(
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
}
