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
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/admin/{userId}/add")
    public ResponseEntity<Notification> createNotification(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Notification notification,
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

        notificationService.createNotification(user, notification);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PutMapping("/admin/{userId}/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id,
            @Valid @RequestBody Notification notification,
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

        notificationService.updateNotification(user, id, notification);
        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/admin/{userId}/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!userService.availabilityOfAction(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        notificationService.deleteNotification(user, id);
        return ResponseEntity.noContent().build();
    }
}
