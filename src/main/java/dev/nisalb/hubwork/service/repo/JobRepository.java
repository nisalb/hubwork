package dev.nisalb.hubwork.service.repo;

import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.JobState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends CrudRepository<Job, Long> {

    @Query("select j from hw_job j " +
            "where (:owner_id is null or j.owner.id = :owner_id)" +
            "  and (:job_state is null or j.state = :job_state)" +
            "  and (:worker_id is null or (j.worker is not null and j.worker.id = :worker_id))")
    Iterable<Job> searchBy(
            @Param("owner_id") Long ownerId,
            @Param("job_state") JobState state,
            @Param("worker_id") Long workerId);
}
