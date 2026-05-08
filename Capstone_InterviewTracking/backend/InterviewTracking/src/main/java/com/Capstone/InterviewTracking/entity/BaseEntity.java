package com.Capstone.InterviewTracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Base entity class that provides a creation timestamp for all entities.
 */
@MappedSuperclass
public class BaseEntity {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Sets the creation timestamp before the entity is saved for the first time.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Returns the timestamp when this entity was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
