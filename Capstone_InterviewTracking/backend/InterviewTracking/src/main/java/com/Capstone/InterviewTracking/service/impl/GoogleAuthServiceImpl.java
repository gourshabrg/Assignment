package com.Capstone.InterviewTracking.service.impl;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.Capstone.InterviewTracking.constant.AppConstants;
import com.Capstone.InterviewTracking.service.GoogleAuthService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Objects;

/**
 * Implementation of GoogleAuthService that loads OAuth2 credentials from credentials.json.
 */
@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Returns a valid Google OAuth2 credential for the Drive API.
     *
     * @return the authorised credential
     * @throws Exception if credentials cannot be loaded or authorisation fails
     */
    @Override
    public Credential getCredentials() throws Exception {
        InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream("credentials.json");

        if (Objects.isNull(in)) {
            throw new RuntimeException(AppConstants.ERR_CREDENTIALS_NOT_FOUND);
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(),
                        new InputStreamReader(in)
                );

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JacksonFactory.getDefaultInstance(),
                        clientSecrets,
                        Collections.singleton(DriveScopes.DRIVE_FILE)
                )
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
