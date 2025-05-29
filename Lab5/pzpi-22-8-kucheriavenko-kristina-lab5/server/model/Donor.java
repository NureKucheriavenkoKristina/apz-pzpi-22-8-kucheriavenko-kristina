package com.BiologicalMaterialsSystem.model;

import com.BiologicalMaterialsSystem.enums.Gender;
import com.BiologicalMaterialsSystem.enums.RhFactorOfBlood;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "donors")
@Getter
@Setter
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donorID;

    @NotNull(message = "First name cannot be null")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    @Column(nullable = false)
    private Date birthDate;

    @NotNull(message = "Gender cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull(message = "ID number cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "ID number must be 10 digits long")
    @Column(nullable = false, unique = true)
    private String idNumber;

    @NotNull(message = "Blood type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RhFactorOfBlood bloodType;

    @Size(max = 500, message = "Transplant restrictions cannot exceed 500 characters")
    @Column(nullable = false)
    private String transplantRestrictions;
}
