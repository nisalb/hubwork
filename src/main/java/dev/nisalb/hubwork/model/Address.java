package dev.nisalb.hubwork.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "hw_address")
@Getter
@Setter
@RequiredArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String houseNo;

    @Column(nullable = false)
    private String streetLineOne;

    private String streetLineTwo;

    @Column(nullable = false)
    private String city;

    private String postalCode;

    private String state;
}
