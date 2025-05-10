package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.enums.StorageZone;
import com.BiologicalMaterialsSystem.model.*;
import com.BiologicalMaterialsSystem.repository.NotificationRepository;
import com.BiologicalMaterialsSystem.repository.StorageConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class StorageConditionService {

    private final StorageConditionRepository repository;
    private final NotificationRepository notificationRepository;
    private final EventLogService eventLogService;
    private final BiologicalMaterialService biologicalMaterialService;

    public void createCondition(User user, StorageCondition condition) {
        processCondition(condition);
        repository.save(condition);
        String logMessage = logMessage(condition, "User with ID " + user.getUserID() + ". Add new ");
        eventLogService.logAction(user, logMessage);
    }

    public void createCondition(StorageCondition condition) {
        processCondition(condition);
        repository.save(condition);
        String logMessage = logMessage(condition, "IOT");
        eventLogService.logAction(null, logMessage);
    }

    private void processCondition(StorageCondition condition) {
        BiologicalMaterial material = biologicalMaterialService.getBiologicalMaterialById(condition.getMaterialID().getMaterialID());

        double score = calculateEnvironmentScore(condition);
        StorageZone zone = determineZone(score);

        condition.setZone(zone);
        condition.setMeasurementTime(new Date());

        if (zone != StorageZone.GREEN) {
            Notification notification = new Notification();
            notification.setEventType("Hazardous storage conditions");
            notification.setDetails("Material in the zone: " + zone.name());
            notification.setNotificationTime(new Date());
            notification.setMaterialID(material);
            notificationRepository.save(notification);
        }
    }

    private String logMessage(StorageCondition condition, String creator) {
        return String.format(
                "storage condition by %s and material with ID: %d | " +
                        "Zone: %s, " +
                        "Oxygen Level: %.2f%%, " +
                        "Humidity: %.2f%%, " +
                        "Temperature: %.2f°C",
                creator,
                condition.getMaterialID().getMaterialID(),
                condition.getZone(),
                condition.getOxygenLevel(),
                condition.getHumidity(),
                condition.getTemperature()
        );
    }

    public double calculateEnvironmentScore(StorageCondition condition) {
        BiologicalMaterial material = biologicalMaterialService.getBiologicalMaterialById(condition.getMaterialID().getMaterialID());

        double idealTemp = material.getIdealTemperature();
        double idealHumidity = material.getIdealHumidity();
        double idealOxygen = material.getIdealOxygenLevel();

        double normTemp = 1 - Math.abs((condition.getTemperature() - idealTemp) / 10.0);
        double normHumidity = 1 - Math.abs((condition.getHumidity() - idealHumidity) / 100.0);
        double normOxygen = 1 - Math.abs((condition.getOxygenLevel() - idealOxygen) / 100.0);

        return (normTemp * 0.5 + normHumidity * 0.3 + normOxygen * 0.2);
    }

    public StorageZone determineZone(double score) {
        if (score < 0.3) return StorageZone.RED;
        if (score < 0.7) return StorageZone.YELLOW;
        return StorageZone.GREEN;
    }

    public StorageCondition getConditionById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Condition not found"));
    }

    public List<StorageCondition> getAllConditions() {
        return repository.findAll();
    }

    public void updateCondition(User user, Long id, StorageCondition newCondition) {
        StorageCondition condition = getConditionById(id);
        condition.setTemperature(newCondition.getTemperature());
        condition.setHumidity(newCondition.getHumidity());
        condition.setOxygenLevel(newCondition.getOxygenLevel());
        condition.setMaterialID(newCondition.getMaterialID());
        processCondition(condition);
        repository.save(condition);
        String logMessage = logMessage(condition, "User with ID " + user.getUserID() + ". Update ");
        eventLogService.logAction(user, logMessage);
    }

    public void deleteCondition(User user, Long id) {
        StorageCondition condition = getConditionById(id);
        repository.deleteById(id);
        String logMessage = logMessage(condition, "User with ID " + user.getUserID() + ". Delete ");
        eventLogService.logAction(user, logMessage);
    }
}
