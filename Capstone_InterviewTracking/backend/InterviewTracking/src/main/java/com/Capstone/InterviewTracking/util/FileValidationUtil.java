package com.Capstone.InterviewTracking.util;

import org.springframework.web.multipart.MultipartFile;

/**
 * Utility class with static methods for validating uploaded files.
 */
public class FileValidationUtil {

    private FileValidationUtil() {}

    /**
     * Validates that the uploaded file is a non-empty PDF document.
     *
     * @param file the uploaded file to validate
     */
    public static void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Resume file is required");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }
    }
}
