package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.analytics_module.projection.IDailySessionCountProjection;
import com.spring.Suwatha.analytics_module.projection.IHourlyUsageProjection;
import com.spring.Suwatha.analytics_module.projection.IStatisticsProjection;
import com.spring.Suwatha.session_module.dto.sessionManagement.SessionListViewDto;
import com.spring.Suwatha.session_module.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> ,SessionRepositoryCustom  {
    
    @Query(
            value = """
            SELECT
                (SELECT COUNT(*) FROM sessions WHERE status = 'ACTIVE') AS activeSessions,
                (SELECT COUNT(*) FROM sessions) AS totalSessions,
                (SELECT COUNT(*) FROM sessions WHERE status = 'COMPLETED') AS finishedSessions,
                (SELECT COUNT(*) FROM sessions WHERE DATE(start_time) = CURDATE()) AS sessionsToday,
                (SELECT COUNT(*) FROM therapists WHERE current_status = 'AVAILABLE' AND is_active = TRUE) AS therapistsAvailable,
                (SELECT COUNT(*) FROM therapists) AS totalTherapists,
                (SELECT COUNT(*) FROM therapists WHERE current_status = 'BUSY' AND is_active = TRUE) AS busyTherapists,
                (SELECT COUNT(*) FROM therapists WHERE current_status = 'OFFLINE' ) AS offlineTherapists,
                (SELECT AVG(rating) FROM session_feedback) AS averageSessionRating
        """,
            nativeQuery = true
    )
    IStatisticsProjection getDashboardStatistics();
    
    
    @Query(
            value = """
            SELECT
                HOUR(s.start_time) AS hourOfDay,
                COUNT(s.id) AS sessionCount
            FROM
                sessions s
            WHERE
                s.start_time IS NOT NULL
            GROUP BY
                HOUR(s.start_time)
            ORDER BY
                hourOfDay ASC
        """,
            nativeQuery = true
    )
    List<IHourlyUsageProjection> getHourlyUsage();
    
    
    
    @Query(
            value = """
            SELECT
                DATE(s.start_time) AS sessionDate,
                COUNT(s.id) AS sessionCount
            FROM
                sessions s
            WHERE
                s.start_time >= :startDate
            GROUP BY
                sessionDate
            ORDER BY
                sessionDate ASC
        """,
            nativeQuery = true
    )
    List<IDailySessionCountProjection> getDailySessionCountsSince(@Param("startDate") LocalDate startDate);
    
    
    @Query(value = """
        SELECT s FROM Session s
        JOIN FETCH s.patient p
        JOIN FETCH s.therapist t
        LEFT JOIN FETCH s.sessionFeedback sf
    """,
            countQuery = "SELECT COUNT(s) FROM Session s")
    Page<Session> findAllForManagementView(Pageable pageable);
    
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.therapist.id = :therapistId AND s.startTime >= :startTime")
    long countSessionsForTherapistSince(Long therapistId, LocalDateTime startTime);
    
}
