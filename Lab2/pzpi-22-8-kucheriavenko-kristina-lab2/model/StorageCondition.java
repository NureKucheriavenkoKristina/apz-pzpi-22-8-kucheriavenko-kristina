package com.BiologicalMaterialsSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "storage_condition")
@Getter
@Setter
public class StorageCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordID;

    @NotNull(message = "Temperature cannot be null")
    @DecimalMin(value = "-100.0", message = "Temperature must be greater than or equal to -100")
    @DecimalMax(value = "100.0", message = "Temperature must be less than or equal to 100")
    @Column(nullable = false)
    private double temperature;

    @NotNull(message = "Oxygen level cannot be null")
    @DecimalMin(value = "0.0", message = "Oxygen level must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Oxygen level must be less than or equal to 100")
    @Column(nullable = false)
    private double oxygenLevel;

    @NotNull(message = "Humidity cannot be null")
    @DecimalMin(value = "0.0", message = "Humidity must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Humidity must be less than or equal to 100")
    @Column(nullable = false)
    private double humidity;

    @NotNull(message = "Measurement time cannot be null")
    @PastOrPresent(message = "Measurement time must be in the past or present")
    @Column(nullable = false)
    private Date measurementTime;

    @ManyToOne
    @JoinColumn(name = "materialID")
    @NotNull(message = "Biological material cannot be null")
    private BiologicalMaterial materialID;
}
