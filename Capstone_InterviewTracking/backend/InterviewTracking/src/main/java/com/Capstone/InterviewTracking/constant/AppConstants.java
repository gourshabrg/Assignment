package com.Capstone.InterviewTracking.constant;

/**
 * Centralised store for application-wide string constants.
 */
public final class AppConstants {

    private AppConstants() {}

    // ── API Base Paths ──────────────────────────────────────────────────────────

    public static final String AUTH_BASE_PATH      = "/auth";
    public static final String HR_BASE_PATH        = "/hr";
    public static final String PANEL_BASE_PATH     = "/panel";
    public static final String CANDIDATE_BASE_PATH = "/candidate";
    public static final String JOB_BASE_PATH       = "/jobs";

    // ── Auth Sub-Paths ──────────────────────────────────────────────────────────

    public static final String REGISTER_PATH      = "/signup";
    public static final String LOGIN_PATH         = "/login";
    public static final String SET_PASSWORD_PATH  = "/set-password";
    public static final String TEST_PATH          = "/test";

    //  Derived Paths 

    /** HR-only job listing endpoint. */
    public static final String JOB_HR_PATH          = JOB_BASE_PATH + "/hr";

    /** Wildcard path for toggling a job's active state. */
    public static final String JOB_TOGGLE_PATH      = JOB_BASE_PATH + "/*/toggle";

    /** Public job listing endpoint. */
    public static final String JOB_LIST_PATH        = JOB_BASE_PATH;

    /** Public single-job retrieval endpoint. */
    public static final String JOB_BY_ID_PATH       = JOB_BASE_PATH + "/*";

    /** Candidate self-service job application endpoint. */
    public static final String CANDIDATE_JOB_APPLY  = CANDIDATE_BASE_PATH + "/apply";

    /** Endpoint for a candidate to retrieve their own application. */
    public static final String CANDIDATE_MY_APPLICATION = CANDIDATE_BASE_PATH + "/my-application";

    /** HR endpoint for onboarding a candidate directly. */
    public static final String HR_CREATE_CANDIDATE  = HR_BASE_PATH + "/candidates";

    //  Security Matchers 

    public static final String AUTH_MATCHER      = AUTH_BASE_PATH      + "/**";
    public static final String HR_MATCHER        = HR_BASE_PATH        + "/**";
    public static final String PANEL_MATCHER     = PANEL_BASE_PATH     + "/**";
    public static final String CANDIDATE_MATCHER = CANDIDATE_BASE_PATH + "/**";
    public static final String JOB_MATCHER       = JOB_BASE_PATH       + "/**";

    // Security Header Constants

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX        = "Bearer ";
    public static final String ROLE_PREFIX          = "ROLE_";
    public static final String ALL_PATHS            = "/**";

    //  Google Drive 

    /** Google Drive folder ID where candidate resumes are stored. */
    public static final String DRIVE_FOLDER_ID = "1XT5Us7SJseIH-MyiKzLsuS17on7c_tZn";

    // Email Configuration 

    /** Base URL used in verification email links (points to the set-password page). */
    public static final String EMAIL_LINK_STRING = "http://127.0.0.1:5500/Capstone_InterviewTracking/frontend/pages/auth/set-password.html?token=";

    /** Subject line used for account verification emails. */
    public static final String EMAIL_SUBJECT = "Complete Your Account Setup – Set Your Password";

    /** Verification token validity period in minutes. */
    public static final Integer SET_TOKEN_EXPIRY = 15;

    //  Email Template Builders 

    /**
     * Builds the HTML body for the account verification email.
     *
     * @param name the recipient's display name
     * @param token the one-time verification token
     * @return the HTML email body
     */
    public static String buildEmailMessage(String name, String token) {
        String link = EMAIL_LINK_STRING + token;

        return "<html>" +
                "<body style='font-family:Arial, sans-serif;'>" +
                "<p>Hi " + name + ",</p>" +
                "<p>Click the button below to complete your registration:</p>" +
                "<a href='" + link + "' " +
                "style='display:inline-block;padding:10px 15px;background-color:blue;color:white;text-decoration:none;border-radius:5px;'>"
                + "Verify Account</a>" +
                "<p>If the button doesn't work, copy this link:</p>" +
                "<p><a href='" + link + "'>" + link + "</a></p>" +
                "<p>Regards,<br>Interview Tracking Team</p>" +
                "</body></html>";
    }

    /**
     * Builds the HTML body for the interview scheduling notification email.
     *
     * @param name the candidate's full name
     * @param round the interview round
     * @param dateTime the scheduled date and time
     * @param panelNames the interviewer names
     * @return the HTML email body
     */
    public static String buildInterviewScheduledMessage(String name, String round,
                                                        String dateTime, String panelNames) {
        return "<html><body style='font-family:Arial, sans-serif;'>" +
                "<p>Hi " + name + ",</p>" +
                "<p>Your <strong>" + round + "</strong> interview has been scheduled.</p>" +
                "<table style='border-collapse:collapse; margin:16px 0;'>" +
                "<tr><td style='padding:8px; font-weight:700;'>Date &amp; Time:</td>" +
                "<td style='padding:8px;'>" + dateTime + "</td></tr>" +
                "<tr><td style='padding:8px; font-weight:700;'>Interviewer:</td>" +
                "<td style='padding:8px;'>" + panelNames + "</td></tr>" +
                "</table>" +
                "<p>Please be available on time. Good luck!</p>" +
                "<p>Regards,<br>Interview Tracking Team</p>" +
                "</body></html>";
    }

    /**
     * Builds the HTML body for the HR-round scheduling notification email.
     *
     * @param hrEmail the HR reviewer's email
     * @param candidateName the candidate's full name
     * @param candidateEmail the candidate's email
     * @param dateTime the scheduled date and time
     * @return the HTML email body
     */
    public static String buildHRInterviewScheduledMessage(String hrEmail, String candidateName,
                                                          String candidateEmail, String dateTime) {
        return "<html><body style='font-family:Arial, sans-serif;'>" +
                "<p>Hi,</p>" +
                "<p>An <strong>HR Round</strong> interview has been scheduled for the following candidate.</p>" +
                "<table style='border-collapse:collapse; margin:16px 0;'>" +
                "<tr><td style='padding:8px; font-weight:700;'>Candidate:</td>" +
                "<td style='padding:8px;'>" + candidateName + "</td></tr>" +
                "<tr><td style='padding:8px; font-weight:700;'>Candidate Email:</td>" +
                "<td style='padding:8px;'>" + candidateEmail + "</td></tr>" +
                "<tr><td style='padding:8px; font-weight:700;'>Date &amp; Time:</td>" +
                "<td style='padding:8px;'>" + dateTime + "</td></tr>" +
                "</table>" +
                "<p>Please ensure you are prepared for the HR evaluation.</p>" +
                "<p>Regards,<br>Interview Tracking System</p>" +
                "</body></html>";
    }
}
