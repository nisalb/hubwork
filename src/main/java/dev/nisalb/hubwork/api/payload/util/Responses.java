package dev.nisalb.hubwork.api.payload.util;

import dev.nisalb.hubwork.api.payload.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class Responses {

    public static ResponseEntity<Object> error(ApiError error) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(error.getCode())) {
            return ResponseEntity.internalServerError().body(error);
        }

        return ResponseEntity.status(error.getCode()).body(error);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<T> created(String url, T body) {
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();

        URI uri = URI.create("http://server.base.url");
        try {
            if (!url.startsWith("/"))
                url = "/" + url;
            uri = URI.create(base + url);
        } catch (Exception ignore) {
        }

        return ResponseEntity.created(uri).body(body);
    }

    public static <T> ResponseEntity<T> badRequest(T body) {
        return ResponseEntity.badRequest().body(body);
    }
}
