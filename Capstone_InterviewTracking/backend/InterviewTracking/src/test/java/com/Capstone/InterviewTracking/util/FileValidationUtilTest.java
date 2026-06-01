package com.Capstone.InterviewTracking.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationUtilTest {

    @Test
    void validatePdf_nullFile_throwsRuntimeException() {
        assertThrows(RuntimeException.class, () -> FileValidationUtil.validatePdf(null));
    }

    @Test
    void validatePdf_emptyFile_throwsRuntimeException() {
        MockMultipartFile empty = new MockMultipartFile("file", "resume.pdf",
                "application/pdf", new byte[0]);

        assertThrows(RuntimeException.class, () -> FileValidationUtil.validatePdf(empty));
    }

    @Test
    void validatePdf_nonPdfContentType_throwsRuntimeException() {
        MockMultipartFile notPdf = new MockMultipartFile("file", "resume.docx",
                "application/msword", "content".getBytes());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> FileValidationUtil.validatePdf(notPdf));
        assertTrue(ex.getMessage().contains("PDF"));
    }

    @Test
    void validatePdf_validPdfFile_doesNotThrow() {
        MockMultipartFile pdf = new MockMultipartFile("file", "resume.pdf",
                "application/pdf", "PDF content".getBytes());

        assertDoesNotThrow(() -> FileValidationUtil.validatePdf(pdf));
    }
}
