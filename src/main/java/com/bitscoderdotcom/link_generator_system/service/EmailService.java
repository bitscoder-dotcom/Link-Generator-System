package com.bitscoderdotcom.link_generator_system.service;

import com.bitscoderdotcom.link_generator_system.dto.EmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailSender);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setSubject(emailDetails.getMessageBody());
            mailMessage.setText(emailDetails.getMessageBody());

            mailSender.send(mailMessage);
            log.info("Message sent to: {}", emailDetails.getRecipient());
            log.info("Message sender: {}", emailSender);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }
}
