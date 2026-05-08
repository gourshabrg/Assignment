package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.Candidate;
import com.Capstone.InterviewTracking.entity.Interview;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.enums.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Interview entities.
 */
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    /**
     * Returns all interviews for a specific candidate.
     *
     * @param candidate the candidate
     * @return list of interviews
     */
    List<Interview> findByCandidate(Candidate candidate);

    /**
     * Returns all interviews where the given panel member is assigned.
     *
     * @param panel the panel member
     * @return list of interviews
     */
    List<Interview> findByPanelsContaining(Panel panel);

    /**
     * Finds the interview for a candidate and a specific round.
     *
     * @param candidate the candidate
     * @param round the interview round
     * @return the interview if found
     */
    Optional<Interview> findByCandidateAndRound(Candidate candidate, InterviewRound round);

    /**
     * Checks if an interview for the given round is already scheduled for a candidate.
     *
     * @param candidate the candidate
     * @param round the interview round
     * @return true if an interview already exists for that round
     */
    boolean existsByCandidateAndRound(Candidate candidate, InterviewRound round);

    /**
     * Returns all interviews of a specific round ordered by date descending.
     *
     * @param round the interview round
     * @return list of interviews
     */
    List<Interview> findByRoundOrderByInterviewDateTimeDesc(InterviewRound round);
}
