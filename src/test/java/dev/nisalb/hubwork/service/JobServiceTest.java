package dev.nisalb.hubwork.service;

import dev.nisalb.hubwork.api.payload.JobPayload;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.PaymentMethod;
import dev.nisalb.hubwork.model.UserRole;
import dev.nisalb.hubwork.service.repo.JobRepository;
import dev.nisalb.hubwork.service.repo.UserRepository;
import dev.nisalb.hubwork.testUtil.Mocker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private JobService jobService;

    private JobPayload goodPayload() {
        var payload = new JobPayload();
        payload.setTitle("A New Job");
        payload.setDescription("This is a new Job");
        payload.setDueDate(new Date(2024, Calendar.APRIL, 24));
        payload.setPrice(BigDecimal.valueOf(187L));
        payload.setCurrency("USD");
        payload.setOwnerId(1L);
        payload.setPaymentMethods(Set.of(PaymentMethod.CREDIT_CARD));

        return payload;
    }

    @Test
    @DisplayName("Getting ALL jobs in the system")
    void testFindAllJobs() {
        var job1 = Mocker.newJob();
        var job2 = Mocker.newJob();

        given(jobRepository.findAll())
                .willReturn(Set.of(job1, job2));

        Set<Job> jobs = jobService.findAllJobs();

        assertTrue(jobs.contains(job1));
        assertTrue(jobs.contains(job2));

        verify(jobRepository)
                .findAll();
    }

    @Test
    @DisplayName("FIltering jobs by OWNER")
    void testSearchJobsByOwner() {
        var owner = Mocker.newUser(UserRole.CUSTOMER);
        var job = Mocker.newJob(owner);

        given(jobRepository.searchBy(owner.getId(), null, null))
                .willReturn(Set.of(job));

        Set<Job> jobs = jobService.searchJobsBy(
                Optional.of(owner.getId()),
                Optional.empty(),
                Optional.empty()
        );

        assertTrue(jobs.contains(job));
        assertEquals(1, jobs.size());

        verify(jobRepository)
                .searchBy(owner.getId(), null, null);
    }

    @Test
    @DisplayName("Filtering jobs by STATE")
    void testSearchJobsByState() {
        var job1 = Mocker.newJob(JobState.GRANTED);
        var job2 = Mocker.newJob(JobState.GRANTED);
        var job3 = Mocker.newJob(JobState.COMPLETED);

        // for GRANTED state
        given(jobRepository.searchBy(null, JobState.GRANTED, null))
                .willReturn(Set.of(job1, job2));

        Set<Job> granted = jobService.searchJobsBy(
                Optional.empty(),
                Optional.of(JobState.GRANTED),
                Optional.empty()
        );

        assertEquals(2, granted.size());
        assertTrue(granted.contains(job1));
        assertTrue(granted.contains(job2));

        verify(jobRepository)
                .searchBy(null, JobState.GRANTED, null);

        // for COMPLETED state
        given(jobRepository.searchBy(null, JobState.COMPLETED, null))
                .willReturn(Set.of(job3));

        Set<Job> completed = jobService.searchJobsBy(
                Optional.empty(),
                Optional.of(JobState.COMPLETED),
                Optional.empty()
        );

        assertEquals(1, completed.size());
        assertTrue(completed.contains(job3));

        verify(jobRepository)
                .searchBy(null, JobState.COMPLETED, null);
    }

    @Test
    @DisplayName("Job creation")
    void testJobCreationForGoodPayload() {
        var payload = goodPayload();

        var theUser = Mocker.newUser(UserRole.CUSTOMER);
        theUser.setId(1L);

        var theJob = new Job();
        theJob.setId(1L);

        given(userRepository.findById(payload.getOwnerId()))
                .willReturn(Optional.of(theUser));
        given(jobRepository.save(theJob))
                .willReturn(theJob);

        var result = jobService.createJob(payload, theJob);

        assertTrue(result.isRight(), "create job failed");

        var job = result.get();
        assertEquals(theJob, job);

        verify(userRepository)
                .findById(payload.getOwnerId());
        verify(jobRepository)
                .save(theJob);
    }

    @Test
    @DisplayName("Fail job creation when owner is not a CUSTOMER")
    void testJobCreationFailedForNonCustomers() {
        var payload = goodPayload();

        var badUser = Mocker.newUser(UserRole.WORKER);
        badUser.setId(1L);

        given(userRepository.findById(payload.getOwnerId()))
                .willReturn(Optional.of(badUser));

        var result = jobService.createJob(payload, new Job());

        assertTrue(result.isLeft(), "job creation successful for some reason");

        var error = result.getLeft();
        assertEquals(HttpStatus.BAD_REQUEST, error.getCode());
        assertEquals("INVALID_USER", error.getTitle());

        verify(userRepository)
                .findById(payload.getOwnerId());
    }

    @Test
    @DisplayName("Fail job creation for non-existent user")
    void testJobCreationFailedForNonExistentUsers() {
        var payload = goodPayload();

        given(userRepository.findById(payload.getOwnerId()))
                .willReturn(Optional.empty());

        var result = jobService.createJob(payload, new Job());

        assertTrue(result.isLeft());

        var error = result.getLeft();
        assertEquals(HttpStatus.BAD_REQUEST, error.getCode());
        assertEquals("INVALID_ID", error.getTitle());

        verify(userRepository)
                .findById(payload.getOwnerId());
    }

    @Test
    @DisplayName("Job update")
    void testJobUpdateForGoodPayload() {
        var payload = new JobPayload();
        payload.setTitle("Changed the title");
        var job = Mocker.newJob();
        Long jobId = 1L;

        given(jobRepository.findById(jobId))
                .willReturn(Optional.of(job));
        given(jobRepository.save(job))
                .willReturn(job);

        var result = jobService.updateJob(jobId, payload);

        assertTrue(result.isRight(), "job update failed");

        var updated = result.get();
        assertEquals(payload.getTitle(), updated.getTitle());

        verify(jobRepository)
                .findById(jobId);
        verify(jobRepository)
                .save(job);
    }
}