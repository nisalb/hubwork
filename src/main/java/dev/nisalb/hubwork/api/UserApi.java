package dev.nisalb.hubwork.api;


import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.UserInformation;
import dev.nisalb.hubwork.model.User;
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

@Tag(name = "users", description = "The user API.")
public interface UserApi {

    @Operation(
            summary = "find all users",
            description = "Find all users in the service.",
            tags = "users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = User.class)))
            )
    })
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<List<Object>> findAllUsers();

    @Operation(
            summary = "create a user",
            description = "Sign up a new user. To be performed by an administrator",
            tags = "users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "user creation failed due to an error in the request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "user creation failed due to server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping(value = "/users", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<Object> createUser(
            @Parameter(description = "Required signup information") @RequestBody UserInformation payload
    );

    @Operation(
            summary = "find a user",
            description = "Find a user by their ID",
            tags = "users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<Object> findUser(
            @Parameter(description = "user id") @PathVariable("id") Long id
    );

    @Operation(
            summary = "delete a user",
            description = "Delete a user",
            tags = "users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            )
    })
    @DeleteMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> deleteUser(
            @Parameter(description = "user id") @PathVariable("id") Long id
    );

    @Operation(
            summary = "update a user",
            description = "Updates an existing user",
            tags = "users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "user modification failed due to an error in the request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "user modification failed due to server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> updateUser(
            @Parameter(description = "user id") @PathVariable("id") Long id,
            @Parameter(description = "updated user payload") @RequestBody UserInformation payload
    );
}
