package dev.nisalb.hubwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(
            name = "job_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "request_job_id_fk")
    )
    @JsonIgnore
    private Job job;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "worker_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "request_worker_id_fk")
    )
    @JsonIgnore
    private User worker;

    @Enumerated(EnumType.STRING)
    private RequestState state;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    @JsonIgnore
    public RequestId getId() {
        return new RequestId(id, job, worker);
    }

    public void setId(RequestId id) {
        this.id = id.getId();
        this.job = id.getJob();
        this.worker = id.getWorker();
    }

    @JsonProperty("worker_id")
    public Long getWorkerId() {
        return worker.getId();
    }

    @JsonProperty("job_id")
    public Long getJobId() {
        return job.getId();
    }

    @JsonProperty("id")
    public UUID getUniqueId() {
        return this.id;
    }
}
