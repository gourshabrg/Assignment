package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.PanelRequest;
import com.Capstone.InterviewTracking.entity.Panel;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.exception.BadRequestException;
import com.Capstone.InterviewTracking.repository.PanelRepository;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.service.impl.EmailServiceImpl;
import com.Capstone.InterviewTracking.service.impl.PanelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanelServiceImplTest {

    @Mock private PanelRepository panelRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailServiceImpl emailService;

    @InjectMocks
    private PanelServiceImpl service;

    private PanelRequest buildRequest() {
        PanelRequest r = new PanelRequest();
        r.setFullName("Alice Smith");
        r.setEmail("alice@example.com");
        r.setPhone("9876543210");
        r.setOrganization("TechCorp");
        r.setDesignation("Senior Engineer");
        return r;
    }

    @Test
    void createPanel_newEmail_savesUserAndPanelAndSendsEmail() {
        PanelRequest req = buildRequest();

        when(panelRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(panelRepository.save(any(Panel.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createPanel(req);

        verify(userRepository).save(any(User.class));
        verify(panelRepository).save(any(Panel.class));
        verify(emailService).sendVerificationMail(eq("alice@example.com"), eq("Alice Smith"), anyString());
    }

    @Test
    void createPanel_panelEmailAlreadyExists_throwsBadRequestException() {
        PanelRequest req = buildRequest();

        when(panelRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.createPanel(req));
        verify(userRepository, never()).save(any());
        verify(panelRepository, never()).save(any());
    }

    @Test
    void createPanel_userEmailAlreadyExists_throwsBadRequestException() {
        PanelRequest req = buildRequest();

        when(panelRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.createPanel(req));
        verify(panelRepository, never()).save(any());
    }
}
