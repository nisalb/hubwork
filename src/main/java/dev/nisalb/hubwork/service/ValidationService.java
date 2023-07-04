package dev.nisalb.hubwork.service;

import dev.nisalb.hubwork.api.payload.ApiError;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ValidationService {

    private final Validator validator;

    public <T> ApiError validate(T payload, String message) {
        var violations = validator.validate(payload);

        if (violations.isEmpty())
            return ApiError.ok();

        Map<String, String> errors = violations.stream()
                .map(violation -> {
                    var defaultMsg = violation.getMessage();
                    var value = violation.getInvalidValue();
                    var m = defaultMsg + " (given = [" + value + "])";

                    String field = "unknown"; // get the violated field name
                    for (Path.Node node : violation.getPropertyPath())
                        field = node.getName();

                    return Map.entry(field, m);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ApiError.builder()
                .code(HttpStatus.BAD_REQUEST)
                .title("INVALID_PAYLOAD")
                .description(message)
                .property("violations", errors)
                .build();
    }
}
