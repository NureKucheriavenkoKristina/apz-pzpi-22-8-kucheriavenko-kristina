package com.BiologicalMaterialsSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportID;

    @NotNull(message = "Report type cannot be null")
    @Size(min = 2, max = 100, message = "Report type must be between 2 and 100 characters")
    @Column(nullable = false)
    private String reportType;

    @NotNull(message = "Creation date cannot be null")
    @PastOrPresent(message = "Creation date must be in the past or present")
    @Column(nullable = false)
    private Date creationDate;

    @NotNull(message = "Text cannot be null")
    @Size(min = 5, max = 1000, message = "Text must be between 5 and 1000 characters")
    @Column(nullable = false)
    private String text;

    @NotNull(message = "File link cannot be null")
    @Size(min = 5, max = 500, message = "File link must be between 5 and 500 characters")
    @Column(nullable = false, unique = true)
    private String fileLink;

    @ManyToOne
    @JoinColumn(name = "eventLogID")
    @NotNull(message = "EventLogID cannot be null")
    private EventLog eventLogID;
}
