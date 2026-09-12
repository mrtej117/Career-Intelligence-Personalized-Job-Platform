package com.job.service.impl;

import com.job.service.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.name}")
    private String fromName;

    @Override
    @Async
    public void sendApplicationConfirmation(String toEmail, String seekerName, String jobTitle, String companyName) {
        String subject = "Application Submitted – " + jobTitle;
        String body = """
                <p>Hi %s,</p>
                <p>Your application for <strong>%s</strong> at <strong>%s</strong> has been successfully submitted.</p>
                <table width="100%%" cellpadding="12" cellspacing="0"
                       style="background:#f9f9f9;border-radius:6px;margin:16px 0;">
                  <tr>
                    <td style="color:#6b7280;font-size:13px;width:40%%;">Job Title</td>
                    <td style="color:#1a1a2e;font-weight:bold;">%s</td>
                  </tr>
                  <tr>
                    <td style="color:#6b7280;font-size:13px;">Company</td>
                    <td style="color:#1a1a2e;font-weight:bold;">%s</td>
                  </tr>
                </table>
                <p>The employer will review your profile and you will be notified of any updates.</p>
                <a href="https://www.jobportal.dev/myjobs"
                   style="background:#4f8ef7;color:white;padding:10px 24px;border-radius:5px;
                          text-decoration:none;display:inline-block;margin-top:16px;">
                  View My Applications
                </a>
                """.formatted(seekerName, jobTitle, companyName, jobTitle, companyName);
        send(toEmail, subject, buildHtml("Application Submitted", body));
    }

    @Override
    @Async
    public void sendApplicationStatusUpdate(String toEmail, String seekerName, String jobTitle, String newStatus) {
        String subject = "Application Update – " + jobTitle;
        String statusColor = switch (newStatus.toUpperCase()) {
            case "ACCEPTED" -> "#16a34a";
            case "REJECTED" -> "#dc2626";
            default -> "#6b7280";
        };
        String body = """
                <p>Hi %s,</p>
                <p>Your application status for <strong>%s</strong> has been updated.</p>
                <table width="100%%" cellpadding="12" cellspacing="0"
                       style="background:#f9f9f9;border-radius:6px;margin:16px 0;">
                  <tr>
                    <td style="color:#6b7280;font-size:13px;width:40%%;">Job Title</td>
                    <td style="color:#1a1a2e;font-weight:bold;">%s</td>
                  </tr>
                  <tr>
                    <td style="color:#6b7280;font-size:13px;">Status</td>
                    <td style="color:%s;font-weight:bold;">%s</td>
                  </tr>
                </table>
                <a href="https://www.jobportal.dev/myjobs"
                   style="background:#4f8ef7;color:white;padding:10px 24px;border-radius:5px;
                          text-decoration:none;display:inline-block;margin-top:16px;">
                  View Application
                </a>
                """.formatted(seekerName, jobTitle, jobTitle, statusColor, newStatus);
        send(toEmail, subject, buildHtml("Application Update", body));
    }

    private String buildHtml(String title, String bodyContent) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background:#f4f6fb;font-family:Arial,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0"
                       style="background:#f4f6fb;padding:32px 0;">
                  <tr><td align="center">
                    <table width="600" cellpadding="0" cellspacing="0"
                           style="max-width:600px;width:100%%;">
                      <tr>
                        <td style="background:#1a1a2e;padding:28px 40px;text-align:center;">
                          <img src="https://res.cloudinary.com/dnqp6wte7/image/upload/v1781093648/favicon_xjeodx.svg"
                               width="48" style="display:block;margin:0 auto 8px auto;" />
                          <span style="color:white;font-size:22px;font-weight:bold;">
                            Job<span style="color:#4f8ef7;">Portal</span>
                          </span>
                        </td>
                      </tr>
                      <tr>
                        <td style="background:#4f8ef7;padding:14px 40px;text-align:center;">
                          <span style="color:white;font-size:16px;font-weight:bold;">%s</span>
                        </td>
                      </tr>
                      <tr>
                        <td style="background:white;padding:32px 40px;color:#333;
                                   font-size:14px;line-height:1.8;">
                          %s
                        </td>
                      </tr>
                      <tr>
                        <td style="background:#1a1a2e;padding:16px 40px;text-align:center;">
                          <span style="color:#9ca3af;font-size:12px;">
                            &copy; 2026 JobPortal &mdash; jobportal.dev
                          </span>
                        </td>
                      </tr>
                    </table>
                  </td></tr>
                </table>
                </body>
                </html>
                """.formatted(title, bodyContent);
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to {} | {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {} | {}: {}", to, subject, e.getMessage());
        }
    }
}
