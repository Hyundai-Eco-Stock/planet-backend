package org.phoenix.planet.util.mail;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendHtmlMail(String to, String subject, String htmlBody) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(to);
            helper.setFrom(new InternetAddress(from, "Planet"));
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true → HTML 지원

            mailSender.send(mimeMessage);
            log.info("[MailUtil] HTML Mail sent to {}", to);
        } catch (Exception e) {
            log.error("[MailUtil] Failed to send HTML mail to {}: {}", to, e.getMessage(), e);
        }
    }
}