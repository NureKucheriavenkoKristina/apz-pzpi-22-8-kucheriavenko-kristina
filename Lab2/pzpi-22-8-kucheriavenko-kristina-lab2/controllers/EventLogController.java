package com.BiologicalMaterialsSystem.controllers;

import java.util.Date;
import java.util.List;

import com.BiologicalMaterialsSystem.enums.Access;
import com.BiologicalMaterialsSystem.model.EventLog;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.service.EventLogService;
import com.BiologicalMaterialsSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/event-logs")
public class EventLogController {

    private final EventLogService eventLogService;
    private final UserService userService;

    @PostMapping("/admin/{userId}/add")
    public ResponseEntity<EventLog> createEventLog(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody EventLog eventLog, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        User adminUser = userService.getUserById(userId);
        String value = eventLog.getActionDetails();
        if (adminUser.getAccessRights() != Access.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        eventLogService.logAction(adminUser, value);
        return ResponseEntity.ok(eventLog);
    }

    @GetMapping("/admin/{userId}/{id}")
    public ResponseEntity<EventLog> getEventLogById(
            @PathVariable("userId") Long userId,
            @PathVariable Long id) {
        User adminUser = userService.getUserById(userId);
        if (adminUser.getAccessRights() != Access.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(eventLogService.getEventLogById(id));
    }

    @GetMapping("/admin/{userId}")
    public ResponseEntity<List<EventLog>> getAllEventLogs(
            @PathVariable("userId") Long userId) {
        User adminUser = userService.getUserById(userId);
        if (adminUser.getAccessRights() != Access.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(eventLogService.getAllEventLogs());
    }

    @PutMapping("/admin/{userId}/{id}")
    public ResponseEntity<EventLog> updateEventLog(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody EventLog eventLog, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        User adminUser = userService.getUserById(userId);
        if (adminUser.getAccessRights() != Access.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        String string = eventLog.getActionDetails();
        Date date = eventLog.getActionTime();
        eventLogService.updateEventLog(eventLog.getCreatorID(), string, date);
        return ResponseEntity.ok(eventLog);
    }

    @DeleteMapping("/admin/{userId}/{id}")
    public ResponseEntity<Void> deleteEventLog(
            @PathVariable("userId") Long userId,
            @PathVariable Long id) {
        User adminUser = userService.getUserById(userId);
        if (adminUser.getAccessRights() != Access.FULL) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        eventLogService.deleteEventLog(id);
        return ResponseEntity.noContent().build();
    }
}
