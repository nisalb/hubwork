package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.payload.ApiError;
import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobApiControllerTest {

    @Value(value = "${local.server.port}")
    private int port;


    @Autowired
    private TestRestTemplate api;

    private String getbaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void findAllJobs() {
        ResponseEntity<JobSet> response = api.getForEntity(getbaseUrl() + "/jobs", JobSet.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createJobSuccessful() {
        var payload = goodPayload();

        ResponseEntity<Job> response = api.postForEntity(
                getbaseUrl() + "/jobs",
                payload,
                Job.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Job created = response.getBody();
        assertNotNull(created, "response body is null");
        assertNotNull(created.getId(), "created job id is null");
    }

    @Test
    void createJobFailsForInvalidPayload() {
        var payload = goodPayload();
        payload.setTitle(null);

        ResponseEntity<ApiError> response = api.postForEntity(
                getbaseUrl() + "/jobs",
                payload,
                ApiError.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiError error = response.getBody();
        assertNotNull(error, "ApiError is expected");
        assertEquals(HttpStatus.BAD_REQUEST, error.getCode());
        assertEquals("INVALID_PAYLOAD", error.getTitle());
    }

    @Test
    void findJobSuccessForExistingJob() {
        var created = createAJobForTest();

        ResponseEntity<Job> response = api.getForEntity(
                getbaseUrl() + "/jobs/" + created.getId(),
                Job.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Job found = response.getBody();
        assertNotNull(found, "response body is null");
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void findJobFailForNonExistentJob() {
        ResponseEntity<Job> response = api.getForEntity(
                getbaseUrl() + "/jobs/" + 101,
                Job.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteJob() {
        var created = createAJobForTest();

        ResponseEntity<Job> before = api.getForEntity(
                getbaseUrl() + "/jobs/" + created.getId(),
                Job.class
        );

        assertEquals(HttpStatus.OK, before.getStatusCode());

        api.delete(URI.create(getbaseUrl() + "/jobs/" + created.getId()));

        ResponseEntity<Job> after = api.getForEntity(
                getbaseUrl() + "/jobs/" + created.getId(),
                Job.class
        );

        assertEquals(HttpStatus.NOT_FOUND, after.getStatusCode());
    }

    @Test
    void updateJob() {
        var created = createAJobForTest();

        var payload = new JobPayload();
        payload.setTitle("Changed");
        payload.setDescription(created.getDescription());
        payload.setState(created.getState());
        payload.setPrice(created.getPrice());
        payload.setCurrency(created.getCurrency());
        payload.setDueDate(created.getDueDate());
        payload.setPaymentMethods(created.getPaymentMethods());
        payload.setState(created.getState());
        payload.setOwnerId(1L); // can't use created.getOwner().getId(), since it is NULL!! for some reason

        String putUrl = getbaseUrl() + "/jobs/" + created.getId();
        ResponseEntity<Job> updated = api.exchange(
                putUrl,
                HttpMethod.PUT,
                RequestEntity.put(URI.create(putUrl)).body(payload),
                Job.class
        );

        assertEquals(HttpStatus.OK, updated.getStatusCode());

        var result = updated.getBody();
        assertNotNull(result);
        assertEquals(payload.getTitle(), result.getTitle());
    }

    private JobPayload goodPayload() {
        var payload = new JobPayload();
        payload.setOwnerId(1L);
        payload.setTitle("My first job");
        payload.setDescription("This is my first job");
        payload.setPrice(BigDecimal.valueOf(1000L));
        payload.setCurrency("USD");
        payload.setPaymentMethods(Set.of(PaymentMethod.CREDIT_CARD));
        payload.setDueDate(
                Date.from(
                        LocalDate.of(2024, Month.APRIL, 21)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()));
        return payload;
    }

    private Job createAJobForTest() {
        var payload = goodPayload();
        ResponseEntity<Job> response = api.postForEntity(
                getbaseUrl() + "/jobs",
                payload,
                Job.class);

        return response.getBody();
    }

    public static class JobSet extends HashSet<Job> {
    }
}