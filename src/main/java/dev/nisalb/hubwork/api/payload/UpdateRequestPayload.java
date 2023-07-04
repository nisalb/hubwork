package dev.nisalb.hubwork.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.nisalb.hubwork.model.RequestState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRequestPayload {

    @JsonProperty("worker_id")
    private Long workerId;

    @NotNull(message = "state is required when updating a request")
    private RequestState state;
}
