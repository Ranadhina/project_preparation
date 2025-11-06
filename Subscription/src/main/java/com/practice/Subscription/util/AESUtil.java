package com.practice.Subscription.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
public class AESUtil {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "MySuperSecretKey"; // must be 16 chars for AES-128
    private static final SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

    public static String encrypt(String input) {
        try {
            if (input == null || input.isEmpty()) return input;

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

            log.debug("[ENCRYPT] '{}' -> '{}'", input, encrypted);
            return encrypted;
        } catch (Exception e) {
            log.error("[ENCRYPT ERROR] Unable to encrypt '{}': {}", input, e.getMessage());
            throw new IllegalStateException("Error while encrypting data", e);
        }
    }

    public static String decrypt(String encrypted) {
        try {
            if (encrypted == null || encrypted.isEmpty()) return encrypted;

            // Skip if value looks like plain text (e.g. numeric or not Base64)
            if (!encrypted.matches("^[A-Za-z0-9+/=]+$")) {
                log.debug("[DECRYPT SKIP] Value '{}' looks unencrypted, skipping decryption.", encrypted);
                return encrypted;
            }

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
            String decrypted = new String(cipher.doFinal(decodedBytes));

            log.debug("[DECRYPT] '{}' -> '{}'", encrypted, decrypted);
            return decrypted;
        } catch (Exception e) {
            log.warn("[DECRYPT WARN] Failed to decrypt '{}': {}. Returning original value.", encrypted, e.getMessage());
            // Return original instead of failing — this keeps app running
            return encrypted;
        }
    }
}
