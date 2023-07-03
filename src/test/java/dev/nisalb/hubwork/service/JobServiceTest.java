package dev.nisalb.hubwork.service;

import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.model.UserRole;
import dev.nisalb.hubwork.service.repo.JobRepository;
import dev.nisalb.hubwork.testUtil.Mocker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    @DisplayName("Test the result of the getting all jobs in the system")
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
    @DisplayName("Test the result when filtering jobs by owner id")
    void testSearchJobsByOwner() {
        var owner = Mocker.newUser(UserRole.CUSTOMER);
        var job = Mocker.newJob(owner);

        given(jobRepository.searchJobsBy(owner.getId(), null, null))
                .willReturn(Set.of(job));

        Set<Job> jobs = jobService.searchJobsBy(
                Optional.of(owner.getId()),
                Optional.empty(),
                Optional.empty()
        );

        assertTrue(jobs.contains(job));
        assertEquals(1, jobs.size());

        verify(jobRepository)
                .searchJobsBy(owner.getId(), null, null);
    }

    @Test
    @DisplayName("Test the result when filtering jobs by state")
    void testSearchJobsByState() {
        var job1 = Mocker.newJob(JobState.GRANTED);
        var job2 = Mocker.newJob(JobState.GRANTED);
        var job3 = Mocker.newJob(JobState.COMPLETED);

        // for GRANTED state
        given(jobRepository.searchJobsBy(null, JobState.GRANTED, null))
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
                .searchJobsBy(null, JobState.GRANTED, null);

        // for COMPLETED state
        given(jobRepository.searchJobsBy(null, JobState.COMPLETED, null))
                .willReturn(Set.of(job3));

        Set<Job> completed = jobService.searchJobsBy(
                Optional.empty(),
                Optional.of(JobState.COMPLETED),
                Optional.empty()
        );

        assertEquals(1, completed.size());
        assertTrue(completed.contains(job3));

        verify(jobRepository)
                .searchJobsBy(null, JobState.COMPLETED, null);
    }
}