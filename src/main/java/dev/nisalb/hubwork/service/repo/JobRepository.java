package dev.nisalb.hubwork.service.repo;

import dev.nisalb.hubwork.model.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, Long> {
}
