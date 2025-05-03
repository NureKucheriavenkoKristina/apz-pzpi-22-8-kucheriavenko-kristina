package com.BiologicalMaterialsSystem.service;


import com.BiologicalMaterialsSystem.model.*;
import com.BiologicalMaterialsSystem.repository.ReportRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final EventLogService eventLogService;

    public void createReport(User user, Report report) {
        reportRepository.save(report);
        eventLogService.logAction(user, "Added new report with ID: " + report.getReportID());
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public void updateReport(User user, Long id, Report newReport) {
        Report report = getReportById(id);
        report.setReportType(newReport.getReportType());
        report.setCreationDate(newReport.getCreationDate());
        report.setText(newReport.getText());
        report.setFileLink(newReport.getFileLink());
        report.setEventLogID(newReport.getEventLogID());
        reportRepository.save(report);
        eventLogService.logAction(user, "Updated report with ID: " + report.getReportID());
    }

    public void deleteReport(User user, Long id) {
        reportRepository.deleteById(id);
        eventLogService.logAction(user, "Deleted report with ID: " + id);
    }
}