package com.BiologicalMaterialsSystem.controllers;

import java.util.List;

import com.BiologicalMaterialsSystem.model.Donor;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.service.DonorService;
import com.BiologicalMaterialsSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/donors")
public class DonorController {
    private final DonorService donorService;
    private final UserService userService;

    @PostMapping("/admin/{userId}/add")
    public ResponseEntity<Donor> createDonor(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Donor donor,
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

        donorService.createDonor(user, donor);

        return ResponseEntity.ok(donor);
    }

    @GetMapping("/{DonorID}")
    public ResponseEntity<Donor> getDonorById(@PathVariable("DonorID") Long id) {
        Donor donor = donorService.getDonorById(id);
        if (donor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(donor);
    }

    @GetMapping
    public ResponseEntity<List<Donor>> getAllDonors() {
        return ResponseEntity.ok(donorService.getAllDonors());
    }

    @PutMapping("/admin/{userId}/{DonorID}")
    public ResponseEntity<Donor> updateDonor(
            @PathVariable("userId") Long userId,
            @PathVariable("DonorID") Long id,
            @Valid @RequestBody Donor donor,
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

        donorService.updateDonor(user, id, donor);

        return ResponseEntity.ok(donor);
    }

    @DeleteMapping("/admin/{userId}/{id}")
    public ResponseEntity<Void> deleteDonor(
            @PathVariable("userId") Long userId,
            @PathVariable("id") Long id) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!userService.availabilityOfAction(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        donorService.deleteDonor(user, id);

        return ResponseEntity.noContent().build();
    }
}
