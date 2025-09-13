package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SessionRepositoryCustom {
    Page<Session> findWithFilters(
            String searchTerm,
            String status,
            String communicationType,
            Pageable pageable
    );
    
    Page<Session> findWithFiltersForTherapist(
            Long therapistId, String searchTerm, String status, String communicationType, Pageable pageable);
}
