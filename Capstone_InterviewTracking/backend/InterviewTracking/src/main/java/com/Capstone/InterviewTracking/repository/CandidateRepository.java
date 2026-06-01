package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Candidate entities.
 */
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    /**
     * Finds a candidate by their email address.
     *
     * @param email the email address
     * @return the candidate if found
     */
    Optional<Candidate> findByEmail(String email);

    /**
     * Finds a candidate by their associated user account.
     *
     * @param user the linked user entity
     * @return the candidate if found
     */
    Optional<Candidate> findByUser(User user);

    /**
     * Returns all candidates who applied for a specific job.
     *
     * @param jobId the job description ID
     * @return list of matching candidates
     */
    List<Candidate> findByJobDescriptionId(Long jobId);

    /**
     * Checks if a candidate with the given email exists.
     *
     * @param email the email address
     * @return true if a candidate exists with that email
     */
    boolean existsByEmail(String email);
}
