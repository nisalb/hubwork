package dev.nisalb.hubwork.model.key;

import dev.nisalb.hubwork.model.Job;
import dev.nisalb.hubwork.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RequestId implements Serializable {
    private UUID id;
    private Job job;
    private User worker;

    public RequestId(UUID id, Job job, User worker) {
        this.id = id;
        this.job = job;
        this.worker = worker;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RequestId that = (RequestId) o;
        return getJob() != null && Objects.equals(getJob(), that.getJob())
                && getWorker() != null && Objects.equals(getWorker(), that.getWorker());
    }

}
