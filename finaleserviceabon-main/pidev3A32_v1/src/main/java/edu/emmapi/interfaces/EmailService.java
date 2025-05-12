package edu.emmapi.interfaces;


public interface EmailService {
    void sendEmail(String to, String subject, String content);
}