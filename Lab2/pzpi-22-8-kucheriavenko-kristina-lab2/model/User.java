package com.BiologicalMaterialsSystem.model;

import com.BiologicalMaterialsSystem.enums.Access;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Column(nullable = false)
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Role cannot be null")
    @Size(min = 2, max = 255, message = "Access rights must be between 2 and 255 characters")
    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @JsonProperty("access_rights")
    @Column(nullable = false)
    private Access accessRights = Access.READ_ONLY;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Login is required")
    @Size(min = 2, max = 100, message = "Login must be between 2 and 100 characters")
    @Email(message = "Login(email) are required")
    private String login;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
