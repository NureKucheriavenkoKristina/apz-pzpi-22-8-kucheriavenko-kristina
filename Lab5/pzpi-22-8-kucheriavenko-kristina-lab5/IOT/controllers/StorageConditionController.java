package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.model.*;
import com.BiologicalMaterialsSystem.repositories.StorageConditionRepository;
import com.BiologicalMaterialsSystem.repositories.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorageConditionService {
    private final StorageConditionRepository repository;
    private final EventLogService eventLogService;

    public void createCondition(User user, StorageCondition condition) {
        repository.save(condition);
        String logMessage = logMessage(condition, "admin with ID " + user.getUserID());
        eventLogService.logAction(user, logMessage);
    }

    public void createCondition(StorageCondition condition) {
        repository.save(condition);
        String logMessage = logMessage(condition, "IOT");
        eventLogService.logAction(null, logMessage);
    }

    private String logMessage(StorageCondition condition, String creator) {
        Map<String, Double> averages = calculateAverageValues(condition.getMaterialID().getMaterialID());
        double temperatureDeviation = condition.getTemperature() - averages.getOrDefault("avgTemperature", 0.0);
        double oxygenLevelDeviation = condition.getOxygenLevel() - averages.getOrDefault("avgOxygenLevel", 0.0);
        double humidityDeviation = condition.getHumidity() - averages.getOrDefault("avgHumidity", 0.0);

        return String.format(
                "Added new storage condition by %s and material with ID: %d | " +
                        "Averages - Temperature: %.2f°C (Deviation: %.2f°C), " +
                        "Oxygen Level: %.2f%% (Deviation: %.2f%%), " +
                        "Humidity: %.2f%% (Deviation: %.2f%%)",
                creator,
                condition.getRecordID(),
                averages.getOrDefault("avgTemperature", 0.0),
                temperatureDeviation,
                averages.getOrDefault("avgOxygenLevel", 0.0),
                oxygenLevelDeviation,
                averages.getOrDefault("avgHumidity", 0.0),
                humidityDeviation
        );
    }

    public Map<String, Double> calculateAverageValues(Long materialId) {
        List<Object[]> averages = repository.findAverageValuesByMaterialId(materialId);
        Map<String, Double> averagesMap = new HashMap<>();
        if (averages != null && !averages.isEmpty()) {
            Object[] avgValues = averages.get(0);
            averagesMap.put("avgTemperature", (Double) avgValues[0]);
            averagesMap.put("avgOxygenLevel", (Double) avgValues[1]);
            averagesMap.put("avgHumidity", (Double) avgValues[2]);
        }
        return averagesMap;
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

        Map<String, Double> averages = calculateAverageValues(newCondition.getMaterialID().getMaterialID());
        double temperatureDeviation = newCondition.getTemperature() - averages.getOrDefault("avgTemperature", 0.0);
        double oxygenLevelDeviation = newCondition.getOxygenLevel() - averages.getOrDefault("avgOxygenLevel", 0.0);
        double humidityDeviation = newCondition.getHumidity() - averages.getOrDefault("avgHumidity", 0.0);

        condition.setTemperature(newCondition.getTemperature());
        condition.setOxygenLevel(newCondition.getOxygenLevel());
        condition.setHumidity(newCondition.getHumidity());
        condition.setMeasurementTime(newCondition.getMeasurementTime());
        condition.setMaterialID(newCondition.getMaterialID());
        repository.save(condition);

        String logMessage = String.format(
                "Updated storage condition with ID: %d | " +
                        "Averages - Temperature: %.2f°C (Deviation: %.2f°C), " +
                        "Oxygen Level: %.2f%% (Deviation: %.2f%%), " +
                        "Humidity: %.2f%% (Deviation: %.2f%%)",
                condition.getRecordID(),
                averages.getOrDefault("avgTemperature", 0.0),
                temperatureDeviation,
                averages.getOrDefault("avgOxygenLevel", 0.0),
                oxygenLevelDeviation,
                averages.getOrDefault("avgHumidity", 0.0),
                humidityDeviation
        );

        eventLogService.logAction(user, logMessage);
    }

    public void deleteCondition(User user, Long id) {
        StorageCondition condition = getConditionById(id);

        Map<String, Double> averages = calculateAverageValues(condition.getMaterialID().getMaterialID());
        double temperatureDeviation = condition.getTemperature() - averages.getOrDefault("avgTemperature", 0.0);
        double oxygenLevelDeviation = condition.getOxygenLevel() - averages.getOrDefault("avgOxygenLevel", 0.0);
        double humidityDeviation = condition.getHumidity() - averages.getOrDefault("avgHumidity", 0.0);

        String logMessage = String.format(
                "Deleted storage condition with ID: %d | " +
                        "Averages - Temperature: %.2f°C (Deviation: %.2f°C), " +
                        "Oxygen Level: %.2f%% (Deviation: %.2f%%), " +
                        "Humidity: %.2f%% (Deviation: %.2f%%)",
                condition.getRecordID(),
                averages.getOrDefault("avgTemperature", 0.0),
                temperatureDeviation,
                averages.getOrDefault("avgOxygenLevel", 0.0),
                oxygenLevelDeviation,
                averages.getOrDefault("avgHumidity", 0.0),
                humidityDeviation
        );

        eventLogService.logAction(user, logMessage);
        repository.deleteById(id);
    }

}
