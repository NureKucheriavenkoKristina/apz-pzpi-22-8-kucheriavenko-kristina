package com.BiologicalMaterialsSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationID;

    @NotNull(message = "Event time cannot be null")
    @PastOrPresent(message = "Event time must be in the past or present")
    @Column(nullable = false)
    private Date eventTime;

    @NotNull(message = "Event type cannot be null")
    @Size(min = 2, max = 100, message = "Event type must be between 2 and 100 characters")
    @Column(nullable = false)
    private String eventType;

    @NotNull(message = "Details cannot be null")
    @Size(min = 5, max = 500, message = "Details must be between 5 and 500 characters")
    @Column(nullable = false)
    private String details;

    @NotNull(message = "Status cannot be null")
    @Size(min = 2, max = 50, message = "Status must be between 2 and 50 characters")
    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "materialID")
    @NotNull(message = "Biological material cannot be null")
    private BiologicalMaterial materialID;
}
