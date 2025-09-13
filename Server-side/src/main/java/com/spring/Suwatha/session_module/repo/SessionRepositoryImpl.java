package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.dto.CommunicationType;
import com.spring.Suwatha.session_module.dto.SessionStatus;
import com.spring.Suwatha.session_module.entity.Patient;
import com.spring.Suwatha.session_module.entity.Session;
import com.spring.Suwatha.user_module.entity.Therapist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SessionRepositoryImpl implements SessionRepositoryCustom {
    
    private final EntityManager em;
    
    public SessionRepositoryImpl(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public Page<Session> findWithFilters(String searchTerm, String status, String communicationType, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        // Query for fetching the actual data
        CriteriaQuery<Session> query = cb.createQuery(Session.class);
        Root<Session> session = query.from(Session.class);
        
        // Eagerly fetch related entities to avoid N+1 problem
        session.fetch("patient", JoinType.INNER);
        session.fetch("therapist", JoinType.INNER);
        session.fetch("sessionFeedback", JoinType.LEFT);
        
        // Build the WHERE clause dynamically
        Predicate predicate = buildPredicate(cb, session, searchTerm, status, communicationType);
        query.where(predicate);
        
        // Apply sorting from Pageable
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("therapistName")) {
                    orders.add(order.isAscending() ? cb.asc(session.get("therapist").get("name")) : cb.desc(session.get("therapist").get("name")));
                } else if (order.getProperty().equals("patientHandle")) {
                    orders.add(order.isAscending() ? cb.asc(session.get("patient").get("anonymousHandle")) : cb.desc(session.get("patient").get("anonymousHandle")));
                } else {
                    orders.add(order.isAscending() ? cb.asc(session.get(order.getProperty())) : cb.desc(session.get(order.getProperty())));
                }
            });
            query.orderBy(orders);
        }
        
        query.distinct(true); // Important when using fetch joins with collections
        
        // Execute the data query with pagination
        TypedQuery<Session> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Session> resultList = typedQuery.getResultList();
        
        // Query for getting the total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Session> countSession = countQuery.from(Session.class);
        Predicate countPredicate = buildPredicate(cb, countSession, searchTerm, status, communicationType);
        countQuery.select(cb.count(countSession)).where(countPredicate);
        Long total = em.createQuery(countQuery).getSingleResult();
        
        return new PageImpl<>(resultList, pageable, total);
    }
    
  
    
    private Predicate buildPredicate(CriteriaBuilder cb, Root<Session> session, String searchTerm, String status, String communicationType) {
        List<Predicate> predicates = new ArrayList<>();
        
        // 1. Search Term Filter (searches therapist name AND patient handle)
        if (searchTerm != null && !searchTerm.isBlank()) {
            Join<Session, Therapist> therapist = session.join("therapist");
            Join<Session, Patient> patient = session.join("patient");
            Predicate therapistNameLike = cb.like(cb.lower(therapist.get("name")), "%" + searchTerm.toLowerCase() + "%");
            Predicate patientHandleLike = cb.like(cb.lower(patient.get("anonymousHandle")), "%" + searchTerm.toLowerCase() + "%");
            predicates.add(cb.or(therapistNameLike, patientHandleLike));
        }
        
        // 2. Status Filter
        if (status != null && !status.isBlank()) {
            try {
                SessionStatus sessionStatus = SessionStatus.valueOf(status.toUpperCase());
                predicates.add(cb.equal(session.get("status"), sessionStatus));
            } catch (IllegalArgumentException e) {
                // Invalid status string provided, ignore this filter
            }
        }
        
        // 3. Communication Type Filter
        if (communicationType != null && !communicationType.isBlank()) {
            try {
                CommunicationType commType = CommunicationType.valueOf(communicationType.toUpperCase());
                predicates.add(cb.equal(session.get("communicationType"), commType));
            } catch (IllegalArgumentException e) {
                // Invalid type string provided, ignore this filter
            }
        }
        
        return cb.and(predicates.toArray(new Predicate[0]));
    }
    
    
    
    // ------------------------------ find With Filters For Therapist -------------------------------------------
    
    @Override
    public Page<Session> findWithFiltersForTherapist(Long therapistId, String searchTerm, String status, String communicationType, Pageable pageable) {
    
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Session> query = cb.createQuery(Session.class);
        Root<Session> session = query.from(Session.class);
    
        session.fetch("patient", JoinType.INNER);
        session.fetch("therapist", JoinType.INNER);
        session.fetch("sessionFeedback", JoinType.LEFT);
    
        Predicate predicate = buildPredicate(cb, session, therapistId, searchTerm, status, communicationType);
        query.where(predicate);
    
        // Apply sorting using the now-included helper method
        applySorting(cb, query, session, pageable.getSort());
    
        query.distinct(true);
    
        // Execute query
        TypedQuery<Session> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize()); // <-- TYPO CORRECTED HERE
        List<Session> resultList = typedQuery.getResultList();
    
        // Get total count
        Long total = getTotalCountForTherapist(cb, therapistId, searchTerm, status, communicationType);
    
        return new PageImpl<>(resultList, pageable, total);
    }
    
    // Helper method for the count query to keep things clean
    private Long getTotalCountForTherapist(CriteriaBuilder cb, Long therapistId, String searchTerm, String status, String communicationType) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Session> countSession = countQuery.from(Session.class);
        Predicate basePredicate = buildPredicate(cb, countSession, searchTerm, status, communicationType);
        Predicate therapistPredicate = cb.equal(countSession.get("therapist").get("id"), therapistId);
        
        countQuery.select(cb.count(countSession)).where(cb.and(basePredicate, therapistPredicate));
        return em.createQuery(countQuery).getSingleResult();
    }
    
    
    
    private Predicate buildPredicate(CriteriaBuilder cb, Root<Session> session, Long therapistId, String searchTerm, String status, String communicationType) {
        List<Predicate> predicates = new ArrayList<>();
        
        // ** ADDED ** Therapist ID Filter
        if (therapistId != null) {
            predicates.add(cb.equal(session.get("therapist").get("id"), therapistId));
        }
        
        // Search Term Filter
        if (searchTerm != null && !searchTerm.isBlank()) {
            Join<Session, Patient> patient = session.join("patient");
            // If therapist ID is not specified (admin search), also search by therapist name
            if (therapistId == null) {
                Join<Session, Therapist> therapist = session.join("therapist");
                Predicate therapistNameLike = cb.like(cb.lower(therapist.get("name")), "%" + searchTerm.toLowerCase() + "%");
                Predicate patientHandleLike = cb.like(cb.lower(patient.get("anonymousHandle")), "%" + searchTerm.toLowerCase() + "%");
                predicates.add(cb.or(therapistNameLike, patientHandleLike));
            } else {
                // If it's a therapist search, they only need to search by patient handle
                predicates.add(cb.like(cb.lower(patient.get("anonymousHandle")), "%" + searchTerm.toLowerCase() + "%"));
            }
        }
        
        // Status Filter
        if (status != null && !status.isBlank()) {
            try {
                SessionStatus sessionStatus = SessionStatus.valueOf(status.toUpperCase());
                predicates.add(cb.equal(session.get("status"), sessionStatus));
            } catch (IllegalArgumentException e) { /* Ignore invalid status */ }
        }
        
        // Communication Type Filter
        if (communicationType != null && !communicationType.isBlank()) {
            try {
                CommunicationType commType = CommunicationType.valueOf(communicationType.toUpperCase());
                predicates.add(cb.equal(session.get("communicationType"), commType));
            } catch (IllegalArgumentException e) { /* Ignore invalid type */ }
        }
        
        return cb.and(predicates.toArray(new Predicate[0]));
    }
    
    // --- THIS IS THE MISSING METHOD ---
    /**
     * A helper method to dynamically apply sorting from a Pageable object to a CriteriaQuery.
     * Handles sorting on nested entity fields like 'therapist.name'.
     */
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Session> query, Root<Session> session, Sort sort) {
        if (sort.isSorted()) {
            List<Order> orders = new ArrayList<>();
            sort.forEach(order -> {
                Path<?> path;
                // Check for nested properties and join if necessary
                if (order.getProperty().equals("therapistName")) {
                    path = session.get("therapist").get("name");
                } else if (order.getProperty().equals("patientHandle")) {
                    path = session.get("patient").get("anonymousHandle");
                } else {
                    // Assume it's a direct property of the Session entity
                    path = session.get(order.getProperty());
                }
                orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
            });
            query.orderBy(orders);
        }
    }
    
}