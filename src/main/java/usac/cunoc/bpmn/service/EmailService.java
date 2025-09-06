package usac.cunoc.bpmn.service;

/**
 * Email service interface for sending emails
 */
public interface EmailService {

    /**
     * Send verification email
     */
    void sendVerificationEmail(String email, String verificationCode);

    /**
     * Send 2FA code
     */
    void send2FACode(String email, String code);

    /**
     * Send password reset email
     */
    void sendPasswordResetEmail(String email, String resetToken);
}