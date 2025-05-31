package com.BiologicalMaterialsSystem.config;

import com.BiologicalMaterialsSystem.model.StorageCondition;
import com.BiologicalMaterialsSystem.service.StorageConditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttListener {

    private final StorageConditionService storageConditionService;
    private final ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<String> message) {
        try {
            String payload = message.getPayload();
            StorageCondition condition = objectMapper.readValue(payload, StorageCondition.class);

            storageConditionService.createCondition(condition);
            System.out.println("Дані збережено в базі: " + condition);
        } catch (Exception e) {
            System.err.println("Помилка при обробці MQTT повідомлення: " + e.getMessage());
        }
    }
}
