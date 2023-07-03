package dev.nisalb.hubwork.service.repo;

import dev.nisalb.hubwork.model.Request;
import dev.nisalb.hubwork.model.key.RequestId;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<Request, RequestId> {
}
