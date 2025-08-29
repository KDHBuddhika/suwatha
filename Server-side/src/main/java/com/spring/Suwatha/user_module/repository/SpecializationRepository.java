package com.spring.Suwatha.user_module.repository;

import com.spring.Suwatha.user_module.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface SpecializationRepository extends JpaRepository<Specialization,Long> {
    Set<Specialization> findByIdIn(List<Long> ids);
}
