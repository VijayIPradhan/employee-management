package com.employee.management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        if (toEmail != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("kichuakshay290@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            mailSender.send(message);
            System.out.println("Mail Sent...");
        } else {
            System.out.println("Email address is null");
        }
    }
    public void sendEmailWithAttachment(String to, String subject, String text, byte [] pdfResource) throws MessagingException, IOException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("kichuakshay290@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment("document.pdf", new ByteArrayResource(pdfResource));
        mailSender.send(mimeMessage);
        System.out.println("Mail Sent...");
    }


}
