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
@RequestMapping("/api/storage-conditions")
public class StorageConditionController {

    private final StorageConditionService storageConditionService;
    private final UserService userService;

    @PostMapping("/admin/{userId}/add")
    public ResponseEntity<StorageCondition> createCondition(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody StorageCondition condition,
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

        storageConditionService.createCondition(user, condition);
        return ResponseEntity.ok(condition);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageCondition> getConditionById(@PathVariable Long id) {
        return ResponseEntity.ok(storageConditionService.getConditionById(id));
    }

    @GetMapping
    public ResponseEntity<List<StorageCondition>> getAllConditions() {
        return ResponseEntity.ok(storageConditionService.getAllConditions());
    }

    @PutMapping("/admin/{userId}/{id}")
    public ResponseEntity<StorageCondition> updateCondition(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id,
            @Valid @RequestBody StorageCondition condition,
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
        storageConditionService.updateCondition(user, id, condition);
        return ResponseEntity.ok(condition);
    }

    @DeleteMapping("/admin/{userId}/{id}")
    public ResponseEntity<Void> deleteCondition(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!userService.availabilityOfAction(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        storageConditionService.deleteCondition(user, id);
        return ResponseEntity.noContent().build();
    }
}
