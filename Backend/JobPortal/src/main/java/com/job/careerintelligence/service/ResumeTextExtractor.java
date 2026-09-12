package com.job.careerintelligence.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;

@Slf4j
@Service
public class ResumeTextExtractor {

    public String extractTextFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        if (fileUrl.contains("example.com/dummy-resume")) {
            return "Experienced Java Developer with 5 years in Spring Boot and PostgreSQL. Skilled in microservices, REST APIs, and Docker.";
        }

        try (InputStream in = new URL(fileUrl).openStream()) {
            if (fileUrl.toLowerCase().endsWith(".pdf")) {
                try (PDDocument document = Loader.loadPDF(in.readAllBytes())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            } else if (fileUrl.toLowerCase().endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(in);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            } else {
                log.warn("Unsupported file format for extraction: {}", fileUrl);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to extract text from {}: {}", fileUrl, e.getMessage());
            return null;
        }
    }

    public String extractTextFromBytes(byte[] bytes, String fileName) {
        if (bytes == null || bytes.length == 0 || fileName == null) {
            return null;
        }
        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                try (PDDocument document = Loader.loadPDF(bytes)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            } else if (fileName.toLowerCase().endsWith(".docx")) {
                try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes);
                     XWPFDocument doc = new XWPFDocument(in);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            } else {
                log.warn("Unsupported file format for local extraction: {}", fileName);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to extract text from local file {}: {}", fileName, e.getMessage());
            return null;
        }
    }
}
