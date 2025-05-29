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
@RequestMapping("/api/biological-materials")
@CrossOrigin(origins = "*")
public class BiologicalMaterialController {

    private final BiologicalMaterialService biologicalMaterialService;
    private final UserService userService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<BiologicalMaterial> createBiologicalMaterial(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody BiologicalMaterial biologicalMaterial,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        biologicalMaterialService.createBiologicalMaterial(user, biologicalMaterial);
        return ResponseEntity.ok(biologicalMaterial);
    }

    @GetMapping("/{materialID}")
    public ResponseEntity<BiologicalMaterial> getBiologicalMaterialById(@PathVariable("materialID") Long materialID) {
        BiologicalMaterial material = biologicalMaterialService.getBiologicalMaterialById(materialID);
        if (material == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(material);
    }

    @GetMapping
    public ResponseEntity<List<BiologicalMaterial>> getAllBiologicalMaterials() {
        List<BiologicalMaterial> materials = biologicalMaterialService.getAllBiologicalMaterials();
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{userId}/{materialID}")
    public ResponseEntity<BiologicalMaterial> updateBiologicalMaterial(
            @PathVariable("userId") Long userId,
            @PathVariable("materialID") Long materialID,
            @Valid @RequestBody BiologicalMaterial biologicalMaterial,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        BiologicalMaterial updatedMaterial = biologicalMaterialService.updateBiologicalMaterial(user, materialID, biologicalMaterial);
        return ResponseEntity.ok(updatedMaterial);
    }

    @DeleteMapping("/admin/{userId}/{materialID}")
    public ResponseEntity<Void> deleteBiologicalMaterial(
            @PathVariable("userId") Long userId,
            @PathVariable("materialID") Long materialID) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        biologicalMaterialService.deleteBiologicalMaterial(user, materialID);
        return ResponseEntity.noContent().build();
    }
}