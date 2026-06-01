package com.Capstone.InterviewTracking.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for uploading files to Google Drive.
 */
public interface DriveService {

    /**
     * Uploads the given file to Google Drive and returns its public URL.
     *
     * @param file the file to upload
     * @return the public Google Drive view URL
     */
    String uploadFile(MultipartFile file);
}
