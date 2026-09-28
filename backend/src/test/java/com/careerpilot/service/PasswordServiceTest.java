package com.careerpilot.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordServiceTest {

    private final PasswordService passwordService = new PasswordService();

    @Test
    void newPasswordsUseBcryptAndVerifyCorrectly() {
        String hash = passwordService.hashPassword("secret123");

        assertThat(hash).startsWith("$2");
        assertThat(passwordService.matches("secret123", hash, PasswordService.BCRYPT_SALT_MARKER)).isTrue();
        assertThat(passwordService.matches("wrong-secret", hash, PasswordService.BCRYPT_SALT_MARKER)).isFalse();
        assertThat(passwordService.needsUpgrade(hash)).isFalse();
    }

    @Test
    void legacySha256PasswordCanStillBeVerifiedAndNeedsUpgrade() throws Exception {
        String legacyHash = legacySha256("secret123", "legacy-salt");

        assertThat(passwordService.matches("secret123", legacyHash, "legacy-salt")).isTrue();
        assertThat(passwordService.matches("wrong-secret", legacyHash, "legacy-salt")).isFalse();
        assertThat(passwordService.needsUpgrade(legacyHash)).isTrue();
    }

    private String legacySha256(String password, String salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest((salt + ":" + password).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
