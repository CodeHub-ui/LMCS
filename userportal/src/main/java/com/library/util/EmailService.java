package com.library.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * EmailService handles sending emails for the Library Management System.
 * Supports various email templates for notifications and verification.
 */
public class EmailService {
    private static final String CONFIG_FILE = "email.properties";
    private Properties emailConfig;
    private Session session;

    public EmailService() {
        loadEmailConfig();
        setupMailSession();
    }

    private void loadEmailConfig() {
        emailConfig = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("❌ Unable to find " + CONFIG_FILE);
                throw new RuntimeException("Email configuration file not found");
            }
            emailConfig.load(input);
        } catch (IOException e) {
            System.err.println("❌ Error loading email configuration: " + e.getMessage());
            throw new RuntimeException("Failed to load email configuration", e);
        }
    }

    private void setupMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getProperty("mail.smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", emailConfig.getProperty("mail.smtp.port", "587"));
        props.put("mail.smtp.auth", emailConfig.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", emailConfig.getProperty("mail.smtp.starttls.enable", "true"));

        final String username = emailConfig.getProperty("mail.username");
        final String password = emailConfig.getProperty("mail.password");

        if (username == null || password == null) {
            throw new RuntimeException("Email username or password not configured");
        }

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Sends an email with the specified parameters.
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getProperty("mail.username")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
            return true;
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends book issue notification email to student.
     */
    public boolean sendBookIssueNotification(String studentEmail, String studentName, String rfid,
                                           String bookName, String issueDate, String dueDate,
                                           String issuedBooksList) {
        String subject = "Library Book Issue Confirmation";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your book has been successfully issued!\n\n" +
            "Student Details:\n" +
            "Name: %s\n" +
            "RFID: %s\n\n" +
            "Book Details:\n" +
            "Book Name: %s\n" +
            "Issue Date: %s\n" +
            "Due Date: %s\n\n" +
            "Currently Issued Books:\n%s\n\n" +
            "Please return the book by the due date to avoid fines.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            studentName, studentName, rfid, bookName, issueDate, dueDate, issuedBooksList
        );

        return sendEmail(studentEmail, subject, body);
    }

    /**
     * Sends book return notification email to student.
     */
    public boolean sendBookReturnNotification(String studentEmail, String studentName, String rfid,
                                            String bookReturned, String returnDate, String remainingBooksList) {
        String subject = "Library Book Return Confirmation";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your book return has been successfully processed!\n\n" +
            "Student Details:\n" +
            "Name: %s\n" +
            "RFID: %s\n\n" +
            "Return Details:\n" +
            "Book Returned: %s\n" +
            "Return Date: %s\n\n" +
            "Remaining Issued Books:\n%s\n\n" +
            "Thank you for returning the book on time.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            studentName, studentName, rfid, bookReturned, returnDate, remainingBooksList
        );

        return sendEmail(studentEmail, subject, body);
    }

    /**
     * Sends student registration confirmation email.
     */
    public boolean sendStudentRegistrationNotification(String studentEmail, String studentName, String rfid) {
        String subject = "Welcome to Library Management System";
        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to the Library Management System!\n\n" +
            "Your registration has been successfully completed.\n\n" +
            "Student Details:\n" +
            "Name: %s\n" +
            "RFID: %s\n\n" +
            "You can now use your RFID card to login and issue books.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            studentName, studentName, rfid
        );

        return sendEmail(studentEmail, subject, body);
    }

    /**
     * Sends faculty registration confirmation email.
     */
    public boolean sendFacultyRegistrationNotification(String facultyEmail, String facultyName, String rfid) {
        String subject = "Welcome to Library Management System - Faculty";
        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to the Library Management System!\n\n" +
            "Your faculty registration has been successfully completed.\n\n" +
            "Faculty Details:\n" +
            "Name: %s\n" +
            "RFID: %s\n\n" +
            "You can now use your RFID card to login and issue books.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            facultyName, facultyName, rfid
        );

        return sendEmail(facultyEmail, subject, body);
    }

    /**
     * Sends book issue notification email to faculty.
     */
    public boolean sendFacultyBookIssueNotification(String facultyEmail, String facultyName, String rfid,
                                           String bookName, String issueDate, String dueDate,
                                           String issuedBooksList) {
        String subject = "Library Book Issue Confirmation - Faculty";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your book has been successfully issued!\n\n" +
            "Faculty Details:\n" +
            "Name: %s\n" +
            "RFID: %s\n\n" +
            "Book Details:\n" +
            "Book Name: %s\n" +
            "Issue Date: %s\n" +
            "Due Date: %s\n\n" +
            "Currently Issued Books:\n%s\n\n" +
            "Please return the book by the due date to avoid fines.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            facultyName, facultyName, rfid, bookName, issueDate, dueDate, issuedBooksList
        );

        return sendEmail(facultyEmail, subject, body);
    }

    /**
     * Sends book return notification email to faculty.
     */
    public boolean sendFacultyBookReturnNotification(String facultyEmail, String facultyName, String rfid,
                                            String bookReturned, String returnDate, String remainingBooksList) {
        String subject = "Library Book Return Confirmation - Faculty";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your book return has been successfully processed!\n\n" +
            "Faculty Details:\n" +
            "Name: %s\n" +
            "RFID: %s\n\n" +
            "Return Details:\n" +
            "Book Returned: %s\n" +
            "Return Date: %s\n\n" +
            "Remaining Issued Books:\n%s\n\n" +
            "Thank you for returning the book on time.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            facultyName, facultyName, rfid, bookReturned, returnDate, remainingBooksList
        );

        return sendEmail(facultyEmail, subject, body);
    }

    /**
     * Sends admin notification for faculty registration.
     */
    public boolean sendAdminFacultyRegistrationNotification(String adminEmail, String facultyName, String facultyId, String facultyEmail) {
        String subject = "New Faculty Registration Notification";
        String body = String.format(
            "Dear Admin,\n\n" +
            "A new faculty member has registered in the Library Management System.\n\n" +
            "Faculty Details:\n" +
            "Name: %s\n" +
            "Faculty ID: %s\n" +
            "Email: %s\n\n" +
            "Please review and approve if necessary.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            facultyName, facultyId, facultyEmail
        );

        return sendEmail(adminEmail, subject, body);
    }

    /**
     * Sends admin notification for faculty book issue.
     */
    public boolean sendAdminFacultyBookIssueNotification(String adminEmail, String facultyName, String facultyId, String bookName, String issueDate) {
        String subject = "Faculty Book Issue Notification";
        String body = String.format(
            "Dear Admin,\n\n" +
            "A faculty member has issued a book.\n\n" +
            "Faculty Details:\n" +
            "Name: %s\n" +
            "Faculty ID: %s\n\n" +
            "Book Details:\n" +
            "Book Name: %s\n" +
            "Issue Date: %s\n\n" +
            "Best regards,\n" +
            "Library Management System",
            facultyName, facultyId, bookName, issueDate
        );

        return sendEmail(adminEmail, subject, body);
    }

    /**
     * Sends admin notification for faculty book return.
     */
    public boolean sendAdminFacultyBookReturnNotification(String adminEmail, String facultyName, String facultyId, String bookName, String returnDate) {
        String subject = "Faculty Book Return Notification";
        String body = String.format(
            "Dear Admin,\n\n" +
            "A faculty member has returned a book.\n\n" +
            "Faculty Details:\n" +
            "Name: %s\n" +
            "Faculty ID: %s\n\n" +
            "Book Details:\n" +
            "Book Name: %s\n" +
            "Return Date: %s\n\n" +
            "Best regards,\n" +
            "Library Management System",
            facultyName, facultyId, bookName, returnDate
        );

        return sendEmail(adminEmail, subject, body);
    }
}
