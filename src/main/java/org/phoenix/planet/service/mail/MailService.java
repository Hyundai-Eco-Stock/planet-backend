package org.phoenix.planet.service.mail;

public interface MailService {

    void sendPasswordChange(String to, String name, String resetUrl);
}