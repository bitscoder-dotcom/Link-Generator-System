package com.bitscoderdotcom.link_generator_system.service;

import com.bitscoderdotcom.link_generator_system.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public void sendEmail (EmailDetails emailDetails) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(emailSender);
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());
            helper.setText(emailDetails.getMessageBody(), true);

            mailSender.send(mimeMessage);
            log.info("Message sent to: {}", emailDetails.getRecipient());
            log.info("Message sender: {}", emailSender);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
