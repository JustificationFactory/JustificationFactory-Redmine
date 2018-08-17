package fr.axonic.avek.redmine.utils;

import fr.axonic.avek.redmine.configuration.ConfigurationDocument;
import fr.axonic.avek.redmine.users.UserIdentity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {

    private final ConfigurationDocument credentials;

    public MailSender(ConfigurationDocument credentials) {
        this.credentials = credentials;
    }

    public void sendEmail(UserIdentity to, String subject, String content) throws MessagingException {
        Message message = new MimeMessage(getSession());

        message.setFrom(new InternetAddress(credentials.getEmailAddress()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to.getEmail()));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");

        Transport.send(message);
    }

    private Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", credentials.getEmailHost());
        properties.put("mail.smtp.port", credentials.getEmailPort());

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(credentials.getEmailAddress(), credentials.getEmailPassword());
            }
        });
    }
}
