package dev.nisalb.hubwork.api.payload;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRequestPayload {

    @NotNull(message = "worker id is mandatory when making a request")
    @JsonProperty("worker_id")
    private Long workerId;
}