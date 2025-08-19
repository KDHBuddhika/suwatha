package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
}
