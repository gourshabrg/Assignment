package com.Capstone.InterviewTracking.service.impl;

import com.Capstone.InterviewTracking.dto.PanelRequest;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.RoleType;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.service.PanelService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of PanelService that creates panel member accounts and sends verification emails.
 */
@Service
public class PanelServiceImpl implements PanelService {

    private final PanelRepository panelRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    /**
     * Creates a PanelServiceImpl with the required repositories and email service.
     *
     * @param panelRepository the panel repository
     * @param userRepository the user repository
     * @param emailService the email service
     */
    public PanelServiceImpl(PanelRepository panelRepository,
                            UserRepository userRepository,
                            EmailServiceImpl emailService) {
        this.panelRepository = panelRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Creates a new panel member and their user account, then sends a verification email.
     *
     * @param request the panel member's details
     */
    @Override
    @Transactional
    public void createPanel(PanelRequest request) {
        if (panelRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("A panel member with this email already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(RoleType.PANEL);
        user.setVerified(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        Panel panel = new Panel();
        panel.setFullName(request.getFullName());
        panel.setEmail(request.getEmail());
        panel.setPhone(request.getPhone());
        panel.setOrganization(request.getOrganization());
        panel.setDesignation(request.getDesignation());
        panel.setUser(user);

        panelRepository.save(panel);

        try {
            emailService.sendVerificationMail(request.getEmail(), request.getFullName(), token);
        } catch (Exception e) {
            System.err.println("Warning: Could not send verification email to "
                    + request.getEmail() + " — " + e.getMessage());
        }
    }
}
