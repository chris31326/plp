package com.example.xin.fileprotector.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSMTP {
    public GMailSMTP() {
    }

    public static void sendEmail(final String recipient, final String subject, final String message) {
        try {
            sendEmailThrows(recipient, subject, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendEmailThrows(final String recipient, final String subject, final String message)
            throws MessagingException
    {
        final String username = GMailLoginInfo.USERNAME;
        final String password = GMailLoginInfo.PASSWORD;

        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.ssl.enable", "true");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", "465");

        final Session session = Session.getInstance(props);

        final MimeMessage msg = new MimeMessage(session);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        msg.setSubject(subject);
        msg.setText(message);

        Transport.send(msg, username, password);
    }
}
