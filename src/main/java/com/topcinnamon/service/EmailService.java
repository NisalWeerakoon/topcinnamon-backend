package com.topcinnamon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${admin.notification.email:admin@topcinnamon.com}")
    private String adminEmail;

    public void sendCustomerNotification(String toEmail, String subject, String messageText) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(messageText);

            // For production, uncomment the line below:
            // mailSender.send(message);

            // Simulation for development
            System.out.println("📧 CUSTOMER EMAIL NOTIFICATION:");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + messageText);
            System.out.println("--- Email sent to customer ---");

        } catch (Exception e) {
            System.err.println("❌ Error sending customer email: " + e.getMessage());
        }
    }

    public void sendAdminNotification(String subject, String messageText) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject(subject);
            message.setText(messageText);

            // For production, uncomment the line below:
            // mailSender.send(message);

            // Simulation for development
            System.out.println("📧 ADMIN NOTIFICATION:");
            System.out.println("To: " + adminEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + messageText);
            System.out.println("--- Admin notification sent ---");

        } catch (Exception e) {
            System.err.println("❌ Error sending admin notification: " + e.getMessage());
        }
    }

    public void sendReviewApprovalNotification(String toEmail, String customerName, String productName) {
        String subject = "Your Review Has Been Approved - G.D. De Silva Sons";
        String message = "Dear " + customerName + ",\n\n" +
                "Thank you for submitting your review for " + productName + ".\n\n" +
                "We're pleased to inform you that your review has been approved and is now visible on our website.\n\n" +
                "We appreciate your feedback and hope to serve you again soon!\n\n" +
                "Best regards,\n" +
                "G.D. De Silva Sons Team";

        sendCustomerNotification(toEmail, subject, message);
    }

    public void sendReviewRejectionNotification(String toEmail, String customerName, String reason) {
        String subject = "Update on Your Review - G.D. De Silva Sons";
        String message = "Dear " + customerName + ",\n\n" +
                "Thank you for submitting your review.\n\n" +
                "After careful consideration, we're unable to publish your review at this time. " +
                (reason != null ? "Reason: " + reason + "\n\n" : "\n\n") +
                "If you have any questions, please don't hesitate to contact us.\n\n" +
                "Best regards,\n" +
                "G.D. De Silva Sons Team";

        sendCustomerNotification(toEmail, subject, message);
    }
}