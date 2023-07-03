package dev.nisalb.hubwork.service;

import com.google.common.collect.Sets;
import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import dev.nisalb.hubwork.service.repo.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class JobService {

    @Setter(onMethod = @__({@Autowired}))
    private JobRepository jobRepository;

    public Set<Job> findAllJobs() {
        var jobs = jobRepository.findAll();
        return asSet(jobs);
    }

    public Set<Job> searchJobsBy(
            Optional<Long> ownerId,
            Optional<JobState> state,
            Optional<Long> workerId
    ) {
        var found = jobRepository.searchJobsBy(
                ownerId.orElse(null),
                state.orElse(null),
                workerId.orElse(null)
        );
        return asSet(found);
    }

    private <T> Set<T> asSet(Iterable<T> elems) {
        return Sets.newHashSet(elems);
    }
}
