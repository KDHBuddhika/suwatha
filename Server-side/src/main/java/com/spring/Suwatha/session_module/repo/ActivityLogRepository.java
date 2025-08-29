package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {
    
//    List<ActivityLog> findTop50ByOrderByTimestampDesc();
    
    List<ActivityLog> findAllByOrderByTimestampDesc(Pageable pageable);
    
    
    @Query("SELECT al FROM ActivityLog al WHERE al.timestamp >= :startOfDay AND al.timestamp <= :endOfDay ORDER BY al.timestamp DESC")
    List<ActivityLog> findByDateRange(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
