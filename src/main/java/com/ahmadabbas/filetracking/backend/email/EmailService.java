package com.ahmadabbas.filetracking.backend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${application.mailing.from:default@email.com}")
    private String fromEmail;

    @Async
    public void sendEmail(String to,
                          String userFullName,
                          @NonNull EmailTemplate emailTemplate,
                          String code,
                          String activationUrl,
                          String subject) throws MessagingException {
        String templateName = emailTemplate.toString();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MULTIPART_MODE_MIXED,
                UTF_8.name());

        Map<String, Object> props = new HashMap<>();
        props.put("user_full_name", userFullName);
        props.put("code", code);
        props.put("activation_url", activationUrl);

        Context context = new Context();
        context.setVariables(props);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(message);
    }

}
