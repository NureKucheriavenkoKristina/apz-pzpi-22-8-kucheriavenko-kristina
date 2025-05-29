package com.BiologicalMaterialsSystem.model;

import com.BiologicalMaterialsSystem.enums.DonationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "biological_materials")
@Setter
@Getter
public class BiologicalMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialID;

    @Column(nullable = false)
    @NotBlank(message = "Material name cannot be blank")
    @Size(min = 2, max = 100, message = "Material name must be between 2 and 100 characters")
    private String materialName;

    @Column(nullable = false)
    @Future(message = "Expiration date must be in the future")
    private Date expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Donation status cannot be null")
    private DonationStatus status;

    @Column(nullable = false)
    @PastOrPresent(message = "Transfer date must be in the past or present")
    private Date transferDate;

    @NotNull(message = "Temperature cannot be null")
    @DecimalMin(value = "-100.0", message = "Temperature must be greater than or equal to -100")
    @DecimalMax(value = "100.0", message = "Temperature must be less than or equal to 100")
    @Column(nullable = false)
    private double idealTemperature;

    @NotNull(message = "Oxygen level cannot be null")
    @DecimalMin(value = "0.0", message = "Oxygen level must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Oxygen level must be less than or equal to 100")
    @Column(nullable = false)
    private double idealOxygenLevel;

    @NotNull(message = "Humidity cannot be null")
    @DecimalMin(value = "0.0", message = "Humidity must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Humidity must be less than or equal to 100")
    @Column(nullable = false)
    private double idealHumidity;

    @ManyToOne
    @JoinColumn(name = "donorID")
    @NotNull(message = "Donor cannot be null")
    private Donor donorID;
}
