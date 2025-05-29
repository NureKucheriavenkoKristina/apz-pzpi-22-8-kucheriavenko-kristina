package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.model.Donor;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.repository.DonorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorService {
    private final DonorRepository donorRepository;
    private final EventLogService eventLogService;

    public void createDonor(User user, Donor donor) {
        donorRepository.save(donor);
        eventLogService.logAction(user, "Added a new donor with an ID: " + donor.getDonorID());
    }

    public Donor getDonorById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found"));
    }

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public void updateDonor(User user, Long id, Donor newDonor) {
        Donor donor = getDonorById(id);
        donor.setFirstName(newDonor.getFirstName());
        donor.setLastName(newDonor.getLastName());
        donor.setBirthDate(newDonor.getBirthDate());
        donor.setGender(newDonor.getGender());
        donor.setIdNumber(newDonor.getIdNumber());
        donor.setBloodType(newDonor.getBloodType());
        donor.setTransplantRestrictions(newDonor.getTransplantRestrictions());
        donorRepository.save(donor);
        eventLogService.logAction(user, "Updated donor with an ID: " + donor.getDonorID());
    }

    public void deleteDonor(User user, Long id) {
        Long copyId = id;
        donorRepository.deleteById(id);
        eventLogService.logAction(user, "Deleted donor with an ID: " + copyId);
    }
}
