package dev.nisalb.hubwork.model;

import dev.nisalb.hubwork.model.key.RequestId;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@IdClass(RequestId.class)
public class Request {

    private Long id;

    private Job job;

    private User worker;

    private RequestState state;

    public RequestId getId() {
        return new RequestId(id, job, worker);
    }

    public void setId(RequestId id) {
        this.id = id.getId();
        this.job = id.getJob();
        this.worker = id.getWorker();
    }
}
