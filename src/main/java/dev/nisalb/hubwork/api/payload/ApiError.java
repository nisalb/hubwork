package dev.nisalb.hubwork.api.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.http.HttpStatus;

import java.util.Map;

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
}
