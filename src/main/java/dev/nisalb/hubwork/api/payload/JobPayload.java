package dev.nisalb.hubwork.api.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.nisalb.hubwork.api.payload.util.PriceDeserializer;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
public class JobPayload {
    @NotBlank
    @Size(max = 80, message = "title must not exceed 80 characters")
    private String title;

    @NotBlank
    @Size(max = 140, message = "description must not exceed 140 characters")
    private String description;

    @Future(message = "due date must be a date in future")
    @JsonProperty("due_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy"
    )
    private Date dueDate;

    @Min(value = 10, message = "minimum price is 10")
    @Max(value = 1000, message = "maximum price is 1000")
    @JsonDeserialize(using = PriceDeserializer.class)
    private BigDecimal price;

    @Size(max = 3, message = "currency code must not exceed 3 characters")
    @JsonProperty("currency_code")
    private String currency;

    @NotNull(message = "owner id is mandatory")
    @JsonProperty("owner_id")
    private Long ownerId;

    @NotEmpty(message = "at least one payment method must be specified")
    @JsonProperty("payment_methods")
    private Set<PaymentMethod> paymentMethods;

    private JobState state;
}
