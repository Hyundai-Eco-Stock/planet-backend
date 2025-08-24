package org.phoenix.planet.service.mail;

public interface MailService {

    void sendPasswordReset(String to, String name, String resetUrl);
}