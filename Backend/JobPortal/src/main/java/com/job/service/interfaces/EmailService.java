package com.job.service.interfaces;

public interface EmailService {

    void sendApplicationConfirmation(String toEmail, String seekerName, String jobTitle, String companyName);

    void sendApplicationStatusUpdate(String toEmail, String seekerName, String jobTitle, String newStatus);
}
