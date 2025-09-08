package com.spring.Suwatha.analytics_module.controller;


import com.spring.Suwatha.analytics_module.dto.reportDto.ReportDetailViewDto;
import com.spring.Suwatha.analytics_module.dto.reportDto.ReportFiltersDto;
import com.spring.Suwatha.analytics_module.dto.reportDto.ReportPageDto;
import com.spring.Suwatha.analytics_module.service.ReportService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
//    @GetMapping
//    public ResponseEntity<ReportPageDto> getReports(
//            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResponseEntity.ok(reportService.getReports(pageable));
//    }
@GetMapping
public ResponseEntity<ReportPageDto> getReports(
        @RequestParam(name = "q", required = false) String searchTerm,
        @RequestParam(name = "month", required = false) String month, // e.g., "2025-01"
        @RequestParam(name = "city", required = false) String city,
        @RequestParam(name = "illness", required = false) String illness,
        @RequestParam(name = "risk", required = false) String risk,
        @RequestParam(name = "age", required = false) Integer age,
        @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
    
    ReportPageDto reports = reportService.getReports(
            searchTerm, month, city, illness, risk, age, pageable);
    
    return ResponseEntity.ok(reports);
}
    
    
    @GetMapping("/filters")
    public ResponseEntity<ReportFiltersDto> getReportFilters() {
        return ResponseEntity.ok(reportService.getReportFilters());
    }
    
    
    
    @GetMapping("/{sessionId}")
    public ResponseEntity<ReportDetailViewDto> getReportById(@PathVariable Long sessionId) {
        return ResponseEntity.ok(reportService.getReportById(sessionId));
    }
    
    
}
