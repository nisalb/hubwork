package dev.nisalb.hubwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(name = "hw_user")
@Getter
@Setter
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private String mobile;

    private String firstName;

    private String lastName;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
