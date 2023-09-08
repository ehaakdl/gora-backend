package com.gora.backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.FrontUrl;
import com.gora.backend.model.EmailMessage;
import com.gora.backend.service.user.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    // todo 템플릿화 시키기 
    public void send(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            
            mimeMessageHelper.setText(emailMessage.getMessage());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
