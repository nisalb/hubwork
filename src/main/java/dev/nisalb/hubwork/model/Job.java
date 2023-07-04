package dev.nisalb.hubwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity(name = "hw_job")
@Getter
@Setter
@RequiredArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Date dueDate;

    private BigDecimal price;

    private String currency;

    @OneToOne
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "owner_id_fk")
    )
    @JsonIgnore
    private User owner;

    @OneToOne
    @JoinColumn(
            name = "worker_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "worker_id_fk")
    )
    @JsonIgnore
    private User worker;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = PaymentMethod.class)
    private Set<PaymentMethod> paymentMethods;

    @CreationTimestamp
    private Date postedAt;

    @Enumerated(EnumType.STRING)
    private JobState state = JobState.PENDING;

    @OneToMany(
            mappedBy = "job",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE}
    )
    @JsonIgnore
    private Set<Request> requests;

    @JsonProperty("owner_id")
    public Long getOwnerId() {
        return owner.getId();
    }

    @JsonProperty("worker_id")
    public Long getWorkerId() {
        if (worker == null)
            return null;
        return worker.getId();
    }
}
