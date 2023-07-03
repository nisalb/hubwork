package dev.nisalb.hubwork.service.repo;

import dev.nisalb.hubwork.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
