package controller.service.impl;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import model.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Sends account registration and login notifications. */
public class AccountEmailNotificationService {
    private static final Logger LOG = Logger.getLogger(AccountEmailNotificationService.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void sendWelcome(User user) {
        send(user, "Đăng ký tài khoản Soundora thành công",
                "Tài khoản Soundora của bạn đã được tạo thành công. Chúc bạn có trải nghiệm mua sắm vui vẻ!");
    }

    public void sendLoginNotification(User user) {
        send(user, "Đăng nhập Soundora thành công",
                "Tài khoản Soundora của bạn vừa đăng nhập thành công lúc "
                        + LocalDateTime.now().format(DATE_FORMAT) + ". Nếu không phải bạn, hãy đổi mật khẩu ngay.");
    }

    private void send(User user, String subject, String notice) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) return;
        try {
            String host = env("SOUNDORA_SMTP_HOST", "smtp.gmail.com");
            String port = env("SOUNDORA_SMTP_PORT", "587");
            String username = env("SOUNDORA_SMTP_USERNAME", "");
            String password = env("SOUNDORA_SMTP_PASSWORD", "");
            String from = env("SOUNDORA_SMTP_FROM", username);
            if (username.isBlank() || password.isBlank() || from.isBlank()) {
                throw new IllegalStateException("SMTP is not configured. Set SOUNDORA_SMTP_USERNAME and SOUNDORA_SMTP_PASSWORD.");
            }

            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.connectiontimeout", "10000");
            properties.put("mail.smtp.timeout", "10000");
            properties.put("mail.smtp.writetimeout", "10000");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "Soundora", StandardCharsets.UTF_8.name()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject(subject, StandardCharsets.UTF_8.name());
            String name = user.getFullName() == null ? "bạn" : escape(user.getFullName());
            message.setContent("<div style='font-family:Arial,sans-serif;color:#24243a;max-width:600px;margin:auto'>"
                    + "<h2 style='color:#6c5ce7'>Soundora</h2><p>Xin chào " + name + ",</p><p>"
                    + escape(notice) + "</p><p>Cảm ơn bạn đã sử dụng website Soundora.</p></div>",
                    "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not send account notification to " + user.getEmail(), e);
        }
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
