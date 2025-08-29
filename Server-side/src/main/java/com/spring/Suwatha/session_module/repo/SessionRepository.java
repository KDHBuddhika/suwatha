package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.analytics_module.projection.IStatisticsProjection;
import com.spring.Suwatha.session_module.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
    
    @Query(
            value = """
            SELECT
                (SELECT COUNT(*) FROM sessions WHERE status = 'ACTIVE') AS activeSessions,
                (SELECT COUNT(*) FROM sessions) AS totalSessions,
                (SELECT COUNT(*) FROM sessions WHERE status = 'COMPLETED') AS finishedSessions,
                (SELECT COUNT(*) FROM sessions WHERE DATE(start_time) = CURDATE()) AS sessionsToday,
                (SELECT COUNT(*) FROM therapists WHERE current_status = 'AVAILABLE' AND is_active = TRUE) AS therapistsAvailable,
                (SELECT COUNT(*) FROM therapists WHERE is_active = TRUE) AS totalTherapists,
                (SELECT COUNT(*) FROM therapists WHERE current_status = 'BUSY' AND is_active = TRUE) AS busyTherapists,
                (SELECT COUNT(*) FROM therapists WHERE current_status = 'OFFLINE' AND is_active = TRUE) AS offlineTherapists,
                (SELECT AVG(rating) FROM session_feedback) AS averageSessionRating
        """,
            nativeQuery = true
    )
    IStatisticsProjection getDashboardStatistics();
    
}
