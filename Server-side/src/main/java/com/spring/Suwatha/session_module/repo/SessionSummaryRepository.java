package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.analytics_module.projection.IIllnessCountProjection;
import com.spring.Suwatha.session_module.entity.SessionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionSummaryRepository extends JpaRepository<SessionSummary,Long>  {
    
    
    @Query("""
        SELECT
            s.identifiedIllness AS illness,
            COUNT(s) AS count
        FROM
            SessionSummary s
        WHERE
            s.identifiedIllness IS NOT NULL AND s.identifiedIllness <> ''
        GROUP BY
            s.identifiedIllness
        ORDER BY
            count DESC
    """)
    List<IIllnessCountProjection> getIllnessCounts();
    
    
    
    
    //------------------- for session report-----------------------
    
    @Query("SELECT DISTINCT s.city FROM SessionSummary s WHERE s.city IS NOT NULL AND s.city <> '' ORDER BY s.city")
    List<String> findDistinctCities();
    
    @Query("SELECT DISTINCT s.identifiedIllness FROM SessionSummary s WHERE s.identifiedIllness IS NOT NULL AND s.identifiedIllness <> '' ORDER BY s.identifiedIllness")
    List<String> findDistinctIllnesses();
    
//    @Query("SELECT new com.spring.Suwatha.analytics_module.dto.reportDto.ReportSummaryDto(" +
//            "COUNT(ss.id), " +
//            "COUNT(DISTINCT p.id), " +
//            "(SELECT COUNT(t) FROM Therapist t WHERE t.isActive = true), " +
//            "AVG(ss.durationInMinutes)) " +
//            "FROM SessionSummary ss JOIN ss.session s JOIN s.patient p")
//    ReportSummaryDto getReportSummaryStatistics();

    
    
    
}
