package com.BiologicalMaterialsSystem.controllers;

import java.util.List;

import com.BiologicalMaterialsSystem.model.*;
import com.BiologicalMaterialsSystem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @PostMapping("/admin/{userId}/add")
    public ResponseEntity<Report> createReport(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Report report,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!userService.availabilityOfAction(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        reportService.createReport(user, report);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PutMapping("/admin/{userId}/{id}")
    public ResponseEntity<Report> updateReport(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id,
            @Valid @RequestBody Report report,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if (!userService.availabilityOfAction(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        reportService.updateReport(user, id, report);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/admin/{userId}/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!userService.availabilityOfAction(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        reportService.deleteReport(user, id);
        return ResponseEntity.noContent().build();
    }
}
