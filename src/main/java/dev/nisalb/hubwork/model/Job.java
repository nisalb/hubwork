package dev.nisalb.hubwork.model;

import jakarta.persistence.*;
import lombok.*;

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

    private String price;

    private String currency;

    @OneToOne
    private User owner;

    @OneToOne
    private User worker;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private Set<PaymentMethod> paymentMethods;

    private Date postedAt;

    @Enumerated(EnumType.STRING)
    private JobState state;
}
