package com.careerpilot.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class PasswordService {

    public static final String BCRYPT_SALT_MARKER = "bcrypt";

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String passwordHash, String salt) {
        if (isBcryptHash(passwordHash)) {
            return passwordEncoder.matches(rawPassword, passwordHash);
        }
        return legacySha256(rawPassword, salt).equals(passwordHash);
    }

    public boolean needsUpgrade(String passwordHash) {
        return !isBcryptHash(passwordHash);
    }

    private boolean isBcryptHash(String passwordHash) {
        return passwordHash != null
                && (passwordHash.startsWith("$2a$")
                || passwordHash.startsWith("$2b$")
                || passwordHash.startsWith("$2y$"));
    }

    private String legacySha256(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(((salt == null ? "" : salt) + ":" + password)
                    .getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("密码校验失败", ex);
        }
    }
}
