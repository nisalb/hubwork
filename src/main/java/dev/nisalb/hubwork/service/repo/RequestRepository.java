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
}
