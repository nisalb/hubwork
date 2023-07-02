package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.UserApi;
import dev.nisalb.hubwork.api.payload.UserInformation;
import dev.nisalb.hubwork.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
public class UserApiController implements UserApi {
    @Override
    public ResponseEntity<Object> createUser(UserInformation payload) {
        return ResponseEntity.ok("createUser");
    }

    @Override
    public ResponseEntity<List<Object>> findAllUsers() {
        return ResponseEntity.ok(List.of("findAllUsers"));
    }

    @Override
    public ResponseEntity<Object> findUser(Long id) {
        return ResponseEntity.ok("findUser:" + id);
    }

    @Override
    public ResponseEntity<Object> deleteUser(Long id) {
        return ResponseEntity.ok("deleteUser:" + id);
    }

    @Override
    public ResponseEntity<Object> updateUser(Long id, UserInformation payload) {
        return ResponseEntity.ok("updateUser:" + id);
    }
}
