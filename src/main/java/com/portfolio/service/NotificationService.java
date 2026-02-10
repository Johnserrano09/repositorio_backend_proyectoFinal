package com.portfolio.service;

import com.portfolio.model.*;
import com.portfolio.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@portfolio.com}")
    private String fromEmail;

    public void sendAdvisoryRequestNotification(User programmer, User external, Advisory advisory) {
        String subject = "Nueva solicitud de asesoría";
        String payload = String.format(
                "El usuario %s (%s) ha solicitado una asesoría para el %s. " +
                        "Comentario: %s",
                external.getName(), external.getEmail(),
                advisory.getScheduledAt().toString(),
                advisory.getRequestComment() != null ? advisory.getRequestComment() : "Sin comentario");

        sendEmail(programmer, subject, payload);
        sendWhatsApp(programmer, "Nueva solicitud de asesoría de " + external.getName());
    }

    public void sendAdvisoryApprovedNotification(User external, User programmer, Advisory advisory) {
        String subject = "Tu asesoría ha sido aprobada";
        String payload = String.format(
                "El programador %s ha aprobado tu solicitud de asesoría para el %s. " +
                        "Mensaje: %s",
                programmer.getName(),
                advisory.getScheduledAt().toString(),
                advisory.getResponseMessage() != null ? advisory.getResponseMessage() : "Sin mensaje");

        sendEmail(external, subject, payload);
        sendWhatsApp(external, "Tu asesoría con " + programmer.getName() + " fue aprobada");
    }

    public void sendAdvisoryRejectedNotification(User external, User programmer, Advisory advisory) {
        String subject = "Tu asesoría ha sido rechazada";
        String payload = String.format(
                "El programador %s ha rechazado tu solicitud de asesoría. " +
                        "Mensaje: %s",
                programmer.getName(),
                advisory.getResponseMessage() != null ? advisory.getResponseMessage() : "Sin mensaje");

        sendEmail(external, subject, payload);
    }

    public void sendAdvisoryReminderNotification(User user, Advisory advisory, String role) {
        String subject = "Recordatorio de asesoría";
        String payload = String.format(
                "Tienes una asesoría programada para mañana a las %s.",
                advisory.getScheduledAt().toLocalTime().toString());

        sendEmail(user, subject, payload);
        sendWhatsApp(user, "Recordatorio: asesoría mañana a las " + advisory.getScheduledAt().toLocalTime());
    }

    private void sendEmail(User user, String subject, String body) {
        NotificationLog notificationLog = NotificationLog.builder()
                .user(user)
                .type(NotificationType.EMAIL)
                .destination(user.getEmail())
                .subject(subject)
                .payload(body)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            if (mailEnabled) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(user.getEmail());
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Email sent to: {}", user.getEmail());
            } else {
                log.info("[Mock Email] To: {}, Subject: {}, Body: {}", user.getEmail(), subject, body);
            }

            notificationLog.setStatus(NotificationStatus.SENT);
            notificationLog.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage(e.getMessage());
        }

        notificationLogRepository.save(notificationLog);
    }

    private void sendWhatsApp(User user, String message) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            log.debug("No phone number for user: {}", user.getEmail());
            return;
        }

        NotificationLog notificationLog = NotificationLog.builder()
                .user(user)
                .type(NotificationType.WHATSAPP)
                .destination(user.getPhone())
                .payload(message)
                .status(NotificationStatus.PENDING)
                .build();

        try {
            // Simulate WhatsApp - just log the message
            log.info("[Mock WhatsApp] To: {}, Message: {}", user.getPhone(), message);

            notificationLog.setStatus(NotificationStatus.SENT);
            notificationLog.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to send WhatsApp to {}: {}", user.getPhone(), e.getMessage());
            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage(e.getMessage());
        }

        notificationLogRepository.save(notificationLog);
    }
}
