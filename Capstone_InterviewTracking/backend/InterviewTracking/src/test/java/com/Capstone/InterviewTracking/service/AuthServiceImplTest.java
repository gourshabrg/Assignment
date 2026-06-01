package com.Capstone.InterviewTracking.service;

import com.Capstone.InterviewTracking.dto.AuthRequest;
import com.Capstone.InterviewTracking.dto.AuthResponse;
import com.Capstone.InterviewTracking.dto.SignupRequest;
import com.Capstone.InterviewTracking.entity.User;
import com.Capstone.InterviewTracking.enums.RoleType;
import com.Capstone.InterviewTracking.exception.EmailAlreadyRegisteredException;
import com.Capstone.InterviewTracking.exception.InvalidCredentialsException;
import com.Capstone.InterviewTracking.exception.UserNotFoundException;
import com.Capstone.InterviewTracking.mapper.UserMapper;
import com.Capstone.InterviewTracking.repository.UserRepository;
import com.Capstone.InterviewTracking.security.JwtUtil;
import com.Capstone.InterviewTracking.service.impl.AuthServiceImpl;
import com.Capstone.InterviewTracking.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private EmailServiceImpl emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User buildVerifiedUser() {
        User u = new User();
        u.setEmail("test@example.com");
        u.setPassword("encodedPass");
        u.setRole(RoleType.CANDIDATE);
        u.setVerified(true);
        return u;
    }

    private User buildUnverifiedUser() {
        User u = new User();
        u.setEmail("test@example.com");
        u.setRole(RoleType.CANDIDATE);
        u.setVerified(false);
        u.setVerificationToken("token123");
        u.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
        return u;
    }

    // ── register ────────────────────────────────────────────────────────────────

    @Test
    void register_newUser_savesUserAndSendsEmail() {
        SignupRequest req = new SignupRequest();
        req.setEmail("new@example.com");
        req.setFullName("New User");

        User mockUser = new User();
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userMapper.toUserForSignup("new@example.com", RoleType.CANDIDATE)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        authService.register(req);

        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationMail(eq("new@example.com"), eq("New User"), anyString());
    }

    @Test
    void register_emailAlreadyVerified_throwsEmailAlreadyRegisteredException() {
        SignupRequest req = new SignupRequest();
        req.setEmail("existing@example.com");
        req.setFullName("Existing");

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(buildVerifiedUser()));

        assertThrows(EmailAlreadyRegisteredException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_existingUnverifiedUser_resendVerificationEmail() {
        SignupRequest req = new SignupRequest();
        req.setEmail("test@example.com");
        req.setFullName("Test User");

        User unverified = buildUnverifiedUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(unverified));
        when(userRepository.save(any(User.class))).thenReturn(unverified);

        authService.register(req);

        verify(emailService).sendVerificationMail(eq("test@example.com"), eq("Test User"), anyString());
    }

    // ── setPassword ─────────────────────────────────────────────────────────────

    @Test
    void setPassword_validToken_setsPasswordAndVerifiesUser() {
        User user = buildUnverifiedUser();
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(10));

        String raw = "myPassword123";
        String base64 = Base64.getEncoder().encodeToString(raw.getBytes());

        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(raw)).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.setPassword("valid-token", base64);

        assertTrue(user.isVerified());
        assertNull(user.getVerificationToken());
        assertNull(user.getTokenExpiry());
        verify(userRepository).save(user);
    }

    @Test
    void setPassword_invalidToken_throwsUserNotFoundException() {
        when(userRepository.findByVerificationToken("bad-token")).thenReturn(Optional.empty());

        String base64 = Base64.getEncoder().encodeToString("pass".getBytes());
        assertThrows(UserNotFoundException.class, () -> authService.setPassword("bad-token", base64));
    }

    @Test
    void setPassword_expiredToken_throwsRuntimeException() {
        User user = buildUnverifiedUser();
        user.setTokenExpiry(LocalDateTime.now().minusMinutes(5));

        when(userRepository.findByVerificationToken("expired")).thenReturn(Optional.of(user));

        String base64 = Base64.getEncoder().encodeToString("pass".getBytes());
        assertThrows(RuntimeException.class, () -> authService.setPassword("expired", base64));
    }

    // ── login ───────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsAuthResponse() {
        String raw = "password123";
        String base64 = Base64.getEncoder().encodeToString(raw.getBytes());

        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword(base64);

        User user = buildVerifiedUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(raw, "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com", "CANDIDATE")).thenReturn("jwt-token");

        AuthResponse response = authService.login(req);

        assertEquals("jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("CANDIDATE", response.getRole());
    }

    @Test
    void login_userNotFound_throwsUserNotFoundException() {
        AuthRequest req = new AuthRequest();
        req.setEmail("notfound@example.com");
        req.setPassword(Base64.getEncoder().encodeToString("pass".getBytes()));

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(req));
    }

    @Test
    void login_userNotVerified_throwsInvalidCredentialsException() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword(Base64.getEncoder().encodeToString("pass".getBytes()));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(buildUnverifiedUser()));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentialsException() {
        String base64 = Base64.getEncoder().encodeToString("wrongpass".getBytes());

        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword(base64);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(buildVerifiedUser()));
        when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }
}
