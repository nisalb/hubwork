package dev.nisalb.hubwork.model;

import dev.nisalb.hubwork.model.key.RequestId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "hw_jobrequest")
@Getter
@Setter
@RequiredArgsConstructor
@IdClass(RequestId.class)
public class Request {

    @Id
    private UUID id;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "job_id",
            foreignKey = @ForeignKey(name = "job_id_fk")
    )
    private Job job;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "worker_id",
            foreignKey = @ForeignKey(name = "worker_id_fk")
    )
    private User worker;

    private RequestState state;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public RequestId getId() {
        return new RequestId(id, job, worker);
    }

    public void setId(RequestId id) {
        this.id = id.getId();
        this.job = id.getJob();
        this.worker = id.getWorker();
    }
}
