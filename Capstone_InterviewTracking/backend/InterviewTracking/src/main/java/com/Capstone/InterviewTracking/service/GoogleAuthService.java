package com.Capstone.InterviewTracking.service;

import com.google.api.client.auth.oauth2.Credential;

/**
 * Service interface for obtaining Google OAuth2 credentials.
 */
public interface GoogleAuthService {

    /**
     * Returns a valid Google OAuth2 credential for the Drive API.
     *
     * @return the authorised credential
     * @throws Exception if credentials cannot be loaded or authorisation fails
     */
    Credential getCredentials() throws Exception;
}
