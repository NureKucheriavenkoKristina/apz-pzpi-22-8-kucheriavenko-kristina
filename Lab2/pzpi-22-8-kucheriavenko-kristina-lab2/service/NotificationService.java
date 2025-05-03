package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.model.Notification;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EventLogService eventLogService;

    public void createNotification(User user, Notification notification) {
        notificationRepository.save(notification);
        eventLogService.logAction(user, "Added new notification with ID: " + notification.getNotificationID());
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void updateNotification(User user, Long id, Notification newNotification) {
        Notification notification = getNotificationById(id);
        notification.setEventTime(newNotification.getEventTime());
        notification.setEventType(newNotification.getEventType());
        notification.setDetails(newNotification.getDetails());
        notification.setStatus(newNotification.getStatus());
        notification.setMaterialID(newNotification.getMaterialID());
        notificationRepository.save(notification);
        eventLogService.logAction(user, "Updated notification with ID: " + notification.getNotificationID());
    }

    public void deleteNotification(User user, Long id) {
        notificationRepository.deleteById(id);
        eventLogService.logAction(user, "Deleted notification with ID: " + id);
    }
}