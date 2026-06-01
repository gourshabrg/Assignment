package com.Capstone.InterviewTracking.service.impl;

import com.Capstone.InterviewTracking.service.DriveService;
import com.Capstone.InterviewTracking.service.GoogleAuthService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.Capstone.InterviewTracking.constant.AppConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

/**
 * Implementation of DriveService that uploads files to Google Drive.
 */
@Service
public class DriveServiceImpl implements DriveService {

    private static final String APPLICATION_NAME = "Interview Tracking";
    private static final String FOLDER_ID = AppConstants.DRIVE_FOLDER_ID;

    @Autowired
    private GoogleAuthService googleAuthService;

    /**
     * Builds and returns an authenticated Google Drive service client.
     *
     * @return the Drive service instance
     * @throws Exception if authentication fails
     */
    private Drive getDriveService() throws Exception {
        Credential credential = googleAuthService.getCredentials();

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Uploads a file to Google Drive and returns its public view URL.
     *
     * @param file the file to upload
     * @return the public Google Drive URL
     */
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Drive driveService = getDriveService();

            File fileMetadata = new File();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList(FOLDER_ID));

            java.io.File tempFile = java.io.File.createTempFile("resume-", ".pdf");
            file.transferTo(tempFile);

            FileContent mediaContent = new FileContent("application/pdf", tempFile);

            File uploadedFile = driveService.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            String fileId = uploadedFile.getId();

            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().create(fileId, permission).execute();

            tempFile.delete();

            return "https://drive.google.com/file/d/" + fileId + "/view";

        } catch (Exception e) {
            throw new RuntimeException("Drive upload failed: " + e.getMessage());
        }
    }
}
