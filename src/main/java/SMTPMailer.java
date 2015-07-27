import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * SMTPMailer
 *
 * @Author Trim Kadriu <trim.kadriu@gmail.com>
 */
public class SMTPMailer {
    private String smtpHostName;
    private Integer smtpHostPort;
    private String smtpAuthUser;
    private String smtpAuthPassword;
    private String smtpAuth;

    public SMTPMailer(String smtpHostName, Integer smtpHostPort, String smtpAuthUser, String smtpAuthPassword, String smtpAuth) {
        this.smtpHostName = smtpHostName;
        this.smtpHostPort = smtpHostPort;
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
        this.smtpAuth = smtpAuth;
    }

    public void send(String subject, String mailContent, String contentType, Address from, Address to) throws Exception {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        System.out.println("SMTPMailer.send() > mail.transport.protocol: smtp");
        props.put("mail.smtp.host", smtpHostName);
        System.out.println("SMTPMailer.send() > mail.smtp.host: " + smtpHostName);
        props.put("mail.smtp.port", smtpHostPort);
        System.out.println("SMTPMailer.send() > mail.smtp.port: " + smtpHostPort);
        props.put("mail.smtp.starttls.enable", "false");
        System.out.println("SMTPMailer.send() > mail.smtp.starttls.enable: false");
        Session mailSession = null;
        if (smtpAuth != null) {
            props.put("mail.smtp.auth", smtpAuth);
            System.out.println("SMTPMailer.send() > mail.smtp.auth: " + smtpAuth);
            Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
            mailSession = Session.getInstance(props, auth);
        } else {
            mailSession = Session.getInstance(props);
        }
        mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();
        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject(subject);
        System.out.println("SMTPMailer.send() > subject.length(): " + subject.length());
        message.setContent(mailContent, contentType);
        System.out.println("SMTPMailer.send() > content.length(): " + mailContent.length());
        System.out.println("SMTPMailer.send() > contentType: " + contentType);
        message.setFrom(from);
        System.out.println("SMTPMailer.send() > from: " + from);
        message.addRecipient(Message.RecipientType.TO, to);
        System.out.println("SMTPMailer.send() > receipent (TO): " + to);
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public void sendMultiple(String subject, String content, String contentType, Address from, Address[] to, Address[] cc) throws Exception {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", smtpHostName);
        props.put("mail.smtp.port", smtpHostPort);
        if (smtpAuth != null) {
            props.put("mail.smtp.auth", smtpAuth);
        }
        Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
        Session mailSession = Session.getInstance(props, auth);
        mailSession.setDebug(true);
        Transport transport = mailSession.getTransport();
        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject(subject);
        message.setContent(content, contentType);
        message.setFrom(from);
        message.addRecipients(Message.RecipientType.TO, to);
        if (cc != null) {
            message.addRecipients(Message.RecipientType.CC, cc);
        }
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    private class SMTPAuthenticator extends Authenticator {
        private String username = null;
        private String password = null;

        private SMTPAuthenticator(String username, String password) {
            this.username = smtpAuthUser;
            this.password = smtpAuthPassword;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
