package dev.nisalb.hubwork.api.impl;

import dev.nisalb.hubwork.api.BusinessApi;
import dev.nisalb.hubwork.api.payload.BusinessInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BusinessApiController implements BusinessApi {
    @Override
    public ResponseEntity<List<Object>> findAllBusinesses() {
        return ResponseEntity.ok(List.of("findAllBusinesses"));
    }

    @Override
    public ResponseEntity<Object> createBusiness(BusinessInformation payload) {
        return ResponseEntity.ok("createBusiness");
    }

    @Override
    public ResponseEntity<Object> findBusiness(Long id) {
        return ResponseEntity.ok("findBusiness:" + id);
    }

    @Override
    public ResponseEntity<Object> deleteBusiness(Long id) {
        return ResponseEntity.ok("deleteBusiness:" + id);
    }

    @Override
    public ResponseEntity<Object> updateBusiness(Long id, BusinessInformation payload) {
        return ResponseEntity.ok("updateBusiness:" + id);
    }
}
