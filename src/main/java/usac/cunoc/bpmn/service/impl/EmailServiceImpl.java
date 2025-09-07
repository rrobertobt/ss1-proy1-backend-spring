package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import usac.cunoc.bpmn.service.EmailService;

/**
 * Email service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("BPMN - Verificación de Email");
            message.setText("Tu código de verificación es: " + verificationCode +
                    "\n\nEste código expira en 15 minutos.");

            javaMailSender.send(message);
            log.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email");
        }
    }

    @Override
    public void send2FACode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("BPMN - Código de Autenticación");
            message.setText("Tu código de autenticación de dos factores es: " + code +
                    "\n\nEste código expira en 5 minutos.");

            javaMailSender.send(message);
            log.info("2FA code sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send 2FA code to: {}", email, e);
            throw new RuntimeException("Failed to send 2FA code");
        }
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("BPMN - Recuperación de Contraseña");
            message.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace:\n\n" +
                    resetLink + "\n\nEste enlace expira en 1 hora.\n\n" +
                    "Si no solicitaste este cambio, puedes ignorar este mensaje.");

            javaMailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email");
        }
    }
}