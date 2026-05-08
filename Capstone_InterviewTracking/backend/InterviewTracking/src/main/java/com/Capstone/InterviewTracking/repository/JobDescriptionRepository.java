package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for JobDescription entities.
 */
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    /**
     * Returns all active job descriptions ordered by creation date descending.
     *
     * @return list of active jobs
     */
    List<JobDescription> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Returns all job descriptions ordered by creation date descending.
     *
     * @return list of all jobs
     */
    List<JobDescription> findAllByOrderByCreatedAtDesc();
}
