package usac.cunoc.bpmn.service;

/**
 * Service interface for data encryption/decryption
 */
public interface EncryptionService {

    /**
     * Encrypt sensitive data
     */
    String encrypt(String data);

    /**
     * Decrypt sensitive data
     */
    String decrypt(String encryptedData);

    /**
     * Generate backup codes for 2FA
     */
    java.util.List<String> generateBackupCodes();
}