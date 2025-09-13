package com.spring.Suwatha.session_module.repo;

import com.spring.Suwatha.session_module.entity.SessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback,Long> {
    @Query("SELECT AVG(sf.rating) FROM SessionFeedback sf WHERE sf.session.therapist.id = :therapistId")
    Double getAverageRatingForTherapist(Long therapistId);
}
