package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.model.BiologicalMaterial;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.repository.BiologicalMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BiologicalMaterialService {
    private final BiologicalMaterialRepository biologicalMaterialRepository;
    private final EventLogService eventLogService;

    public void createBiologicalMaterial(User user, BiologicalMaterial biologicalMaterial) {
        biologicalMaterialRepository.save(biologicalMaterial);
        eventLogService.logAction(user, "Added new biological material with ID: " + biologicalMaterial.getMaterialID());
    }

    public BiologicalMaterial getBiologicalMaterialById(Long id) {
        return biologicalMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Biological Material not found"));
    }

    public List<BiologicalMaterial> getAllBiologicalMaterials() {
        return biologicalMaterialRepository.findAll();
    }

    public BiologicalMaterial updateBiologicalMaterial(User user, Long id, BiologicalMaterial newBiologicalMaterial) {
        BiologicalMaterial biologicalMaterial = getBiologicalMaterialById(id);
        biologicalMaterial.setMaterialName(newBiologicalMaterial.getMaterialName());
        biologicalMaterial.setExpirationDate(newBiologicalMaterial.getExpirationDate());
        biologicalMaterial.setStatus(newBiologicalMaterial.getStatus());
        biologicalMaterial.setTransferDate(newBiologicalMaterial.getTransferDate());
        biologicalMaterial.setDonorID(newBiologicalMaterial.getDonorID());
        biologicalMaterialRepository.save(biologicalMaterial);
        eventLogService.logAction(user, "Updated biological material with ID: " + biologicalMaterial.getMaterialID());
        return biologicalMaterial;
    }

    public void deleteBiologicalMaterial(User user, Long id) {
        biologicalMaterialRepository.deleteById(id);
        eventLogService.logAction(user, "Deleted biological material with ID: " + id);
    }
}