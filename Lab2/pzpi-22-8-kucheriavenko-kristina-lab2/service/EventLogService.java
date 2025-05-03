package com.BiologicalMaterialsSystem.service;

import com.BiologicalMaterialsSystem.model.EventLog;
import com.BiologicalMaterialsSystem.model.User;
import com.BiologicalMaterialsSystem.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventLogService {
    private final EventLogRepository eventLogRepository;

    public void logAction(User user, String actionDetails) {
        EventLog eventLog = new EventLog();
        eventLog.setActionDetails(actionDetails);
        eventLog.setActionTime(new Date());
        eventLog.setCreatorID(user);
        eventLogRepository.save(eventLog);
    }

    public void updateEventLog(User user, String actionDetails, Date newDate) {
        EventLog eventLog = new EventLog();
        eventLog.setActionDetails(actionDetails);
        eventLog.setActionTime(newDate);
        eventLog.setCreatorID(user);
        eventLogRepository.save(eventLog);
    }

    public EventLog getEventLogById(Long id) {
        return eventLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event Log not found"));
    }

    public List<EventLog> getAllEventLogs() {
        return eventLogRepository.findAll();
    }

    public void deleteEventLog(Long id) {
        eventLogRepository.deleteById(id);
    }
}