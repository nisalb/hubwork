# hubwork
A demo freelancing REST API

## Caveats

I have used several non-standard practices while implementing this to save time. While some are not optimal, some are my personal preferences.
In addition to the following, there could be other bad or non-standard practices. But these are the significant ones I can think of.

### Not using DTOs

I'm not using DTOs but endpoint-specific payloads and carefully serialized entity objects for this API. 

Endpoint-specific payload is helpful since this is a small project; with them, I could evolve my API as I wrote it. 
Separate validations are possible for separate endpoints with their payload type being different. See:

- [`JobPayload`](1)
- [`CreateRequestPayload`](2)
- [`UpdateRequestPayload`](3)

Since my JPA entities are directly exposed through API, I have used `@JsonIgnore` and `@JsonProperty` to transform my entities
into a more suitable shape at serialization. See:

- [`Job`](4)
- [`Request`](5)
- [`User`](6)

### Directly using Hibernate-Validator

Here, I have deviated from the standard practice of using `@Valid` with `@ControllerAdvice` or `@ExceptionHandler` to implement validations.
I'm using my own [`ApiError`](7) inspired by the `ProblemDetail` specification. While using an exception handler with ProblemDetail would be the
more architecturally clean approach, I chose to use this `ApiError`:

1) It gives me more control over the error responses
2) Since this is a small API, I don't have to create an unnecessary array of exception types, which then I would use with exception handlers

## Things I could not do on time

### Tests for Request management.

I could not write the test cases for `RequestService` and `RequestApiController`. Currently, the only way to test these functionalities is with
the swagger UI.

### Mail notification.

This is under development at the time of writing this document. I'll push it under a different branch when completed.

[1]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/api/payload/JobPayload.java)
[2]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/api/payload/CreateRequestPayload.java)
[3]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/api/payload/UpdateRequestPayload.java)
[4]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/model/Job.java)
[5]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/model/Request.java)
[6]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/model/User.java)
[7]:(https://github.com/reified/hubwork/blob/main/src/main/java/dev/nisalb/hubwork/api/payload/ApiError.java)
