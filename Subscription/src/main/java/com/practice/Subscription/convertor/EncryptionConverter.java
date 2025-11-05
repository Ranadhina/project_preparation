package com.practice.Subscription.convertor;

import com.practice.Subscription.util.AESUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = false)
public class EncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) return null;
        try {
            String encrypted = AESUtil.encrypt(attribute);
            log.debug("[DB SAVE] '{}' -> '{}'", attribute, encrypted);
            return encrypted;
        } catch (Exception e) {
            log.error("[DB ENCRYPT ERROR] {}", e.getMessage());
            throw new IllegalStateException("Error during encryption", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            String decrypted = AESUtil.decrypt(dbData);
            log.debug("[DB READ] '{}' -> '{}'", dbData, decrypted);
            return decrypted;
        } catch (Exception e) {
            log.warn("[DB DECRYPT WARN] Failed for '{}': {}. Returning raw value.", dbData, e.getMessage());
            return dbData;
        }
    }
}
