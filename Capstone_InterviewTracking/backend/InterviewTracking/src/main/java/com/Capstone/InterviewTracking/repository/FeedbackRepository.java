package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.Feedback;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.Panel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Feedback entities.
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Returns all feedback records for a specific interview.
     *
     * @param interview the interview
     * @return list of feedback entries
     */
    List<Feedback> findByInterview(Interview interview);

    /**
     * Checks if a panel member has already submitted feedback for an interview.
     *
     * @param interview the interview
     * @param panel the panel member
     * @return true if feedback from that panel member already exists
     */
    boolean existsByInterviewAndPanel(Interview interview, Panel panel);

    /**
     * Returns all feedback for a list of interviews.
     *
     * @param interviews the list of interviews
     * @return combined list of feedback records
     */
    List<Feedback> findByInterviewIn(List<Interview> interviews);

    /**
     * Checks if an HR reviewer has already submitted feedback for an interview.
     *
     * @param interview the interview
     * @param hrReviewer the HR reviewer's email
     * @return true if the reviewer has already submitted feedback
     */
    boolean existsByInterviewAndHrReviewer(Interview interview, String hrReviewer);
}
