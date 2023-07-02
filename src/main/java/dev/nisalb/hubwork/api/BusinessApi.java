package dev.nisalb.hubwork.api;

import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.BusinessInformation;
import dev.nisalb.hubwork.model.Business;
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

@Tag(name = "businesses", description = "The business API.")
public interface BusinessApi {
    @Operation(
            summary = "find all Business",
            description = "Find all businesses in the service",
            tags = "businesses"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Business.class)))
            )
    })
    @GetMapping(value = "/businesses", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<List<Object>> findAllBusinesses();

    @Operation(
            summary = "create a business",
            description = "Post a new business to the service",
            tags = "businesses"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Business.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "business creation failed due to an error in the request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "business creation failed due to server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping(value = "/businesses", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<Object> createBusiness(
            @Parameter(description = "information of the business to be created") @RequestBody BusinessInformation payload
    );

    @Operation(
            summary = "find a business",
            description = "Find a business by ID",
            tags = "businesses"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Business.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @GetMapping(value = "/businesses/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<Object> findBusiness(
            @Parameter(description = "business id to be found") @PathVariable("id") Long id
    );

    @Operation(
            summary = "delete a business",
            description = "Delete a business",
            tags = "businesses"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Business.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @DeleteMapping(value = "/businesses/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> deleteBusiness(
            @Parameter(description = "business id to be deleted") @PathVariable("id") Long id
    );

    @Operation(
            summary = "update a business",
            description = "Updates an existing business",
            tags = "businesses"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Business.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "business modification failed due to an error in the request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "business modification failed due to server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping(value = "/businesses/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> updateBusiness(
            @Parameter(description = "business id to be updated") @PathVariable("id") Long id,
            @Parameter(description = "new business information") @RequestBody BusinessInformation payload
    );
}
