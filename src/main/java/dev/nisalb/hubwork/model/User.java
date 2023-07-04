package dev.nisalb.hubwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "business_id",
            foreignKey = @ForeignKey(name = "business_id_fk")
    )
    @JsonIgnore
    private Business business;

    @JsonProperty("business_id")
    private Long getBusinessId() {
        if (business == null)
            return null;

        return business.getId();
    }
}
