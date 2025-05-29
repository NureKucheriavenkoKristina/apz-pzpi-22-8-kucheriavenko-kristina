package com.BiologicalMaterialsSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "eventLogs")
@Getter
@Setter
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventLogID;

    @NotNull(message = "Action cannot be null")
    @Size(min = 2, max = 1000, message = "Action must be between 2 and 255 characters")
    @Column(nullable = false)
    private String actionDetails;

    @NotNull(message = "Action time cannot be null")
    @PastOrPresent(message = "Action time must be in the past or present")
    @Column(nullable = false)
    private Date actionTime;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User creatorID;
}
