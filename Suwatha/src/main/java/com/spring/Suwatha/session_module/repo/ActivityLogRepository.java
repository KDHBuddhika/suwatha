package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {
    
    List<ActivityLog> findTop50ByOrderByTimestampDesc();
}
