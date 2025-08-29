package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.SessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback,Long> {
}
