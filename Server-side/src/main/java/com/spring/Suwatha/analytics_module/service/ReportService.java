package com.spring.Suwatha.analytics_module.service;

import com.spring.Suwatha.analytics_module.dto.reportDto.*;
import com.spring.Suwatha.analytics_module.repository.ReportQueryRepository;
import com.spring.Suwatha.session_module.dto.RiskAssessment;
import com.spring.Suwatha.session_module.entity.SessionSummary;
import com.spring.Suwatha.session_module.repo.SessionSummaryRepository;
import com.spring.Suwatha.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {
    
    private final SessionSummaryRepository summaryRepository;
    private final ReportQueryRepository reportQueryRepository; // *** INJECT NEW REPO ***
    
    public ReportService(SessionSummaryRepository summaryRepository, ReportQueryRepository reportQueryRepository) {
        this.summaryRepository = summaryRepository;
        this.reportQueryRepository = reportQueryRepository;
    }
    
//    public ReportPageDto getReports(Pageable pageable) {
//        // In a real app with filters, you'd use a custom repository method here.
//        // For now, we'll use findAll.
//        Page<SessionSummary> summaryPage = summaryRepository.findAll(pageable);
//
//        List<ReportListViewDto> reports = summaryPage.getContent().stream()
//                .map(this::convertToReportListViewDto)
//                .collect(Collectors.toList());
//
//        PaginationDto pagination = PaginationDto.fromPage(summaryPage);
//        ReportSummaryDto summary = summaryRepository.getReportSummaryStatistics();
//
//        return new ReportPageDto(reports, pagination, summary);
//    }
    
    public ReportFiltersDto getReportFilters() {
        List<String> cities = summaryRepository.findDistinctCities();
        List<String> illnesses = summaryRepository.findDistinctIllnesses();
        List<String> riskAssessments = Arrays.stream(RiskAssessment.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return new ReportFiltersDto(cities, illnesses, riskAssessments);
    }
    
    public ReportDetailViewDto getReportById(Long sessionId) {
        SessionSummary summary = summaryRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Report for session ID " + sessionId + " not found."));
        return convertToReportDetailViewDto(summary);
    }
    
    // --- Mappers ---
    private ReportListViewDto convertToReportListViewDto(SessionSummary summary) {
        ReportListViewDto dto = new ReportListViewDto();
        dto.setSessionId(summary.getId());
        dto.setPatientHandle(summary.getSession().getPatient().getAnonymousHandle());
        dto.setTherapistName(summary.getSession().getTherapist().getName());
        dto.setDate(summary.getSession().getStartTime().toLocalDate());
        dto.setIllness(summary.getIdentifiedIllness());
        dto.setCity(summary.getCity());
        dto.setGender(summary.getGender() != null ? summary.getGender().name() : null);
        dto.setAge(summary.getAge());
        dto.setRiskAssessment(summary.getRiskAssessment() != null ? summary.getRiskAssessment().name() : null);
        return dto;
    }
    
    private ReportDetailViewDto convertToReportDetailViewDto(SessionSummary summary) {
        ReportDetailViewDto dto = new ReportDetailViewDto();
        dto.setSessionId(summary.getId());
        dto.setPatientHandle(summary.getSession().getPatient().getAnonymousHandle());
        dto.setTherapistName(summary.getSession().getTherapist().getName());
        dto.setDate(summary.getSession().getStartTime());
        dto.setDurationInMinutes(summary.getDurationInMinutes());
        dto.setCommunicationType(summary.getSession().getCommunicationType().name());
        dto.setCity(summary.getCity());
        dto.setIllness(summary.getIdentifiedIllness());
        dto.setGender(summary.getGender() != null ? summary.getGender().name() : null);
        dto.setAge(summary.getAge());
        dto.setRiskAssessment(summary.getRiskAssessment() != null ? summary.getRiskAssessment().name() : null);
        dto.setClinicalNotes(summary.getTherapistPrivateNotes());
        return dto;
    }
    
    
    public ReportPageDto getReports(
            String searchTerm, String month, String city,
            String illness, String risk, Integer age,
            Pageable pageable) {
        
        // Use the new repository for the filtered data
        Page<SessionSummary> summaryPage = reportQueryRepository.findReportsWithFilters(
                searchTerm, month, city, illness, risk, age, pageable);
        
        List<ReportListViewDto> reports = summaryPage.getContent().stream()
                .map(this::convertToReportListViewDto)
                .collect(Collectors.toList());
        
        PaginationDto pagination = PaginationDto.fromPage(summaryPage);
        // Use the new repository for the filtered summary
        ReportSummaryDto summary = reportQueryRepository.getSummaryStatisticsWithFilters(
                searchTerm, month, city, illness, risk, age);
        
        return new ReportPageDto(reports, pagination, summary);
    }

}
