package dev.nisalb.hubwork.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(name = "hw_business")
@Getter
@Setter
@RequiredArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = {CascadeType.ALL})
    private Address address;

    @Column(nullable = false, unique = true)
    private String ABN;

    @Column(nullable = false, unique = true)
    private String ACN;
}
