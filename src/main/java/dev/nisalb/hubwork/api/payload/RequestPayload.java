package dev.nisalb.hubwork.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nisalb.hubwork.model.RequestState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestPayload {

    @NotNull(message = "job id is mandatory for making requests")
    @JsonProperty("job_id")
    private Long jobID;

    @NotNull(message = "worker id is mandatory for making requests")
    @JsonProperty("worker_id")
    private Long workerId;

    @NotNull(message = "state is required when modifying a job request")
    private RequestState state;
}
