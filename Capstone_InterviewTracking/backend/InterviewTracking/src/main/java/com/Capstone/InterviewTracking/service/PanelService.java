package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.PanelRequest;

/**
 * Service interface for managing interview panel members.
 */
public interface PanelService {

    /**
     * Creates a new panel member and sends a verification email.
     *
     * @param request the panel member's details
     */
    void createPanel(PanelRequest request);
}
