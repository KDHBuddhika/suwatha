package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.SessionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionSummaryRepository extends JpaRepository<SessionSummary,Long> {
}
