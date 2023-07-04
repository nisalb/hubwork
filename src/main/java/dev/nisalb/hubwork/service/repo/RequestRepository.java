package dev.nisalb.hubwork.service.repo;

import dev.nisalb.hubwork.model.Request;
import dev.nisalb.hubwork.model.key.RequestId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends CrudRepository<Request, RequestId> {

    @Query("select r from hw_jobrequest r where r.id = :uuid")
    Optional<Request> findByUniqueId(@Param("uuid") UUID uuid);

    @Query("select r from hw_jobrequest r " +
            "where (:job_id is null or r.job.id = :job_id)" +
            "  and (cast(:req_id as org.hibernate.type.UUIDCharType) is null or r.id = :req_id) " +
            "  and (:state is null or r.state = :state) ")
    Iterable<Request> searchBy(
            @Param("job_id") Long jobId,
            @Param("req_id") UUID reqId,
            @Param("state") String state
    );
}
