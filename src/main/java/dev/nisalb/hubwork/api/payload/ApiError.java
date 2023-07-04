package dev.nisalb.hubwork.api.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.RequestState;
import dev.nisalb.hubwork.model.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;

@Data
@Builder
public class ApiError {
    @Builder.Default
    @JsonIgnore
    private boolean isError = true;

    private HttpStatus code;
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @Singular
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> properties;

    public static ApiError ok() {
        return ApiError
                .builder()
                .isError(false)
                .build();
    }

    private static final Map<JobState, String> invalidJoBStateErrorMessages = Map.ofEntries(
            entry(JobState.PENDING, "Job is still pending."),
            entry(JobState.GRANTED, "Job is already granted."),
            entry(JobState.COMPLETED, "Job is completed."),
            entry(JobState.CANCELLED, "Job is cancelled."));

    public static ApiError noSuchJob(Long jobId) {
        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_ID")
                .description("No such job for id: " + jobId)
                .build();
    }

    public static ApiError invalidJobState(JobState state) {

        String errorMessage = invalidJoBStateErrorMessages.get(state);

        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_JOB_STATE")
                .description(errorMessage)
                .build();
    }

    public static ApiError noSuchUser(Long id) {
        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_ID")
                .description("No such user for id: " + id)
                .build();
    }

    public static ApiError invalidUserRole(UserRole role) {
        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_USER")
                .description("specified user is not a " + role.name())
                .build();
    }

    public static ApiError duplicateRequest(UUID id) {
        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("DUPLICATE_REQUEST")
                .description("This request is already created: " + id)
                .build();
    }

    public static ApiError noSuchRequest(UUID id) {
        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_ID")
                .description("No such request for id: " + id)
                .build();
    }

    public static ApiError internalServerError() {
        return ApiError.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR)
                .title("UNKNOWN_ERROR")
                .description("Unknown server error occurred.")
                .build();
    }

    public static ApiError invalidRequestStateTransition(RequestState from, RequestState to) {
        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_REQUEST_STATE")
                .description("Invalid request state update from '" + from.name() + "' to '" + to.name() + "'")
                .build();
    }
}
