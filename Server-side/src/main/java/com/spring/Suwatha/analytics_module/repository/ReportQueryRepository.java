package com.spring.Suwatha.analytics_module.repository;

import com.spring.Suwatha.analytics_module.dto.reportDto.ReportSummaryDto;
import com.spring.Suwatha.session_module.dto.RiskAssessment;
import com.spring.Suwatha.session_module.entity.Patient;
import com.spring.Suwatha.session_module.entity.Session;
import com.spring.Suwatha.session_module.entity.SessionSummary;
import com.spring.Suwatha.user_module.entity.Therapist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable; // Correct import
import org.springframework.data.domain.Sort;   // Correct import
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportQueryRepository {
    
    private final EntityManager em;
    
    public ReportQueryRepository(EntityManager em) {
        this.em = em;
    }
    
    public Page<SessionSummary> findReportsWithFilters(String searchTerm, String month, String city, String illness, String risk, Integer age, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SessionSummary> query = cb.createQuery(SessionSummary.class);
        Root<SessionSummary> summary = query.from(SessionSummary.class);
        
        // Fetch related entities to prevent N+1 queries
        summary.fetch("session", JoinType.INNER)
                .fetch("patient", JoinType.INNER);
        summary.fetch("session", JoinType.INNER)
                .fetch("therapist", JoinType.INNER);
        
        Predicate predicate = buildPredicate(cb, summary, summary.join("session"), searchTerm, month, city, illness, risk, age);
        query.where(predicate);
        applySorting(cb, query, summary, pageable.getSort());
        query.distinct(true);
        
        TypedQuery<SessionSummary> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<SessionSummary> resultList = typedQuery.getResultList();
        
        Long total = getTotalCount(cb, searchTerm, month, city, illness, risk, age);
        
        return new PageImpl<>(resultList, pageable, total);
    }
    
    public ReportSummaryDto getSummaryStatisticsWithFilters(String searchTerm, String month, String city, String illness, String risk, Integer age) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<SessionSummary> summary = query.from(SessionSummary.class);
        Join<SessionSummary, Session> session = summary.join("session");
        Join<Session, Patient> patient = session.join("patient");
        
        Predicate predicate = buildPredicate(cb, summary, session, searchTerm, month, city, illness, risk, age);
        
        query.multiselect(
                cb.count(summary.get("id")),
                cb.countDistinct(patient.get("id")),
                cb.avg(summary.get("durationInMinutes"))
        ).where(predicate);
        
        Object[] result = em.createQuery(query).getSingleResult();
        long activeTherapists = getActiveTherapistCount();
        
        return new ReportSummaryDto(
                (Long) (result[0] != null ? result[0] : 0L),
                (Long) (result[1] != null ? result[1] : 0L),
                activeTherapists,
                result[2] != null ? (Double) result[2] : 0.0
        );
    }
    
    // --- FULL IMPLEMENTATION OF HELPER METHODS ---
    
    private long getActiveTherapistCount() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Therapist> therapist = query.from(Therapist.class);
        query.select(cb.count(therapist))
                .where(cb.equal(therapist.get("isActive"), true));
        return em.createQuery(query).getSingleResult();
    }
    
    private Long getTotalCount(CriteriaBuilder cb, String searchTerm, String month, String city, String illness, String risk, Integer age) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<SessionSummary> countSummary = countQuery.from(SessionSummary.class);
        Predicate countPredicate = buildPredicate(cb, countSummary, countSummary.join("session"), searchTerm, month, city, illness, risk, age);
        countQuery.select(cb.countDistinct(countSummary)).where(countPredicate); // Use countDistinct for accuracy with joins
        return em.createQuery(countQuery).getSingleResult();
    }
    
    private Predicate buildPredicate(CriteriaBuilder cb, Root<SessionSummary> summary, Join<SessionSummary, Session> session,
                                     String searchTerm, String month, String city, String illness, String risk, Integer age) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Session, Therapist> therapist = session.join("therapist");
        Join<Session, Patient> patient = session.join("patient");
        
        if (searchTerm != null && !searchTerm.isBlank()) {
            Predicate therapistNameLike = cb.like(cb.lower(therapist.get("name")), "%" + searchTerm.toLowerCase() + "%");
            Predicate patientHandleLike = cb.like(cb.lower(patient.get("anonymousHandle")), "%" + searchTerm.toLowerCase() + "%");
            predicates.add(cb.or(therapistNameLike, patientHandleLike));
        }
        
        if (month != null && !month.isBlank()) {
            try {
                YearMonth yearMonth = YearMonth.parse(month); // Expects "YYYY-MM"
                LocalDate start = yearMonth.atDay(1);
                LocalDate end = yearMonth.atEndOfMonth();
                predicates.add(cb.between(session.get("startTime").as(LocalDate.class), start, end));
            } catch (DateTimeParseException e) { /* Ignore invalid month format */ }
        }
        
        if (city != null && !city.isBlank()) {
            predicates.add(cb.equal(cb.lower(summary.get("city")), city.toLowerCase()));
        }
        if (illness != null && !illness.isBlank()) {
            predicates.add(cb.equal(cb.lower(summary.get("identifiedIllness")), illness.toLowerCase()));
        }
        if (risk != null && !risk.isBlank()) {
            try {
               RiskAssessment riskEnum = RiskAssessment.valueOf(risk.toUpperCase());
                predicates.add(cb.equal(summary.get("riskAssessment"), riskEnum));
            } catch (IllegalArgumentException e) { /* Ignore invalid risk string */ }
        }
        if (age != null) {
            predicates.add(cb.equal(summary.get("age"), age));
        }
        
        return cb.and(predicates.toArray(new Predicate[0]));
    }
    
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<SessionSummary> query, Root<SessionSummary> summary, Sort sort) {
        if (sort.isSorted()) {
            List<Order> orders = new ArrayList<>();
            Join<SessionSummary, Session> session = summary.join("session", JoinType.LEFT); // Re-join for sorting
            sort.forEach(order -> {
                try {
                    Expression<?> sortPath;
                    String property = order.getProperty();
                    
                    // Handle sorting on joined tables
                    if ("date".equals(property)) {
                        sortPath = session.get("startTime");
                    } else if ("therapistName".equals(property)) {
                        sortPath = session.get("therapist").get("name");
                    } else if ("patientHandle".equals(property)) {
                        sortPath = session.get("patient").get("anonymousHandle");
                    } else {
                        // Default to sorting on the summary table
                        sortPath = summary.get(property);
                    }
                    
                    orders.add(order.isAscending() ? cb.asc(sortPath) : cb.desc(sortPath));
                } catch (IllegalArgumentException e) {
                    // This can happen if frontend tries to sort by a non-existent property.
                    // We can choose to ignore it or log it.
                }
            });
            query.orderBy(orders);
        }
    }
}