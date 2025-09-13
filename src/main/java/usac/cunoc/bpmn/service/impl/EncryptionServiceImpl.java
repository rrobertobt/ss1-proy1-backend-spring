package usac.cunoc.bpmn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import usac.cunoc.bpmn.service.EncryptionService;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Service for encrypting/decrypting sensitive data like credit card information
 * Fixed AES key length issue by normalizing key to exactly 32 bytes
 */
@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService {

    @Value("${app.encryption.key:bpmn-encryption-key-32-chars!!}")
    private String encryptionKey;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int AES_KEY_LENGTH = 32; // 256 bits

    /**
     * Normalizes the encryption key to exactly 32 bytes for AES-256
     */
    private byte[] normalizeKey(String key) {
        try {
            // Use SHA-256 to create a consistent 32-byte key from any input
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] hashedKey = sha.digest(keyBytes);

            // Ensure exactly 32 bytes
            return Arrays.copyOf(hashedKey, AES_KEY_LENGTH);
        } catch (Exception e) {
            log.error("Error normalizing encryption key", e);
            throw new RuntimeException("Error processing encryption key");
        }
    }

    @Override
    public String encrypt(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Data to encrypt cannot be null or empty");
        }

        try {
            byte[] normalizedKey = normalizeKey(encryptionKey);
            SecretKeySpec secretKey = new SecretKeySpec(normalizedKey, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("Error encrypting data", e);
            throw new RuntimeException("Error encrypting data");
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            throw new IllegalArgumentException("Encrypted data cannot be null or empty");
        }

        try {
            byte[] normalizedKey = normalizeKey(encryptionKey);
            SecretKeySpec secretKey = new SecretKeySpec(normalizedKey, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);

            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            log.error("Error decrypting data", e);
            throw new RuntimeException("Error decrypting data");
        }
    }

    @Override
    public List<String> generateBackupCodes() {
        List<String> backupCodes = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            // Generate 8-character backup codes
            StringBuilder code = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                code.append(random.nextInt(10));
            }
            backupCodes.add(code.toString());
        }

        return backupCodes;
    }
}