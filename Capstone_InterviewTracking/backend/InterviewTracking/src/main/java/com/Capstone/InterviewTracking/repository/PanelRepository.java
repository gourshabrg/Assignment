package com.Capstone.InterviewTracking.repository;

import com.Capstone.InterviewTracking.entity.Panel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Panel entities.
 */
public interface PanelRepository extends JpaRepository<Panel, Long> {

    /**
     * Checks if a panel member with the given email already exists.
     *
     * @param email the email address
     * @return true if a panel member with that email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a panel member with the given phone number already exists.
     *
     * @param phone the phone number
     * @return true if a panel member with that phone exists
     */
    boolean existsByPhone(String phone);

    /**
     * Finds a panel member by their email address.
     *
     * @param email the email address
     * @return the panel member if found
     */
    Optional<Panel> findByEmail(String email);
}
