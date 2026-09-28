package com.careerpilot.service;

import com.careerpilot.common.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthSessionService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final Pattern SUBJECT_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*\"?(\\d+)\"?");
    private static final Pattern EXPIRES_AT_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");
    private static final Pattern ISSUER_PATTERN = Pattern.compile("\"iss\"\\s*:\\s*\"([^\"]*)\"");

    private final String jwtSecret;
    private final String issuer;
    private final long expirationSeconds;

    public AuthSessionService(
            @Value("${career-pilot.auth.jwt-secret:career-pilot-dev-secret-change-me}") String jwtSecret,
            @Value("${career-pilot.auth.issuer:career-pilot}") String issuer,
            @Value("${career-pilot.auth.expiration-seconds:86400}") long expirationSeconds) {
        this.jwtSecret = jwtSecret == null || jwtSecret.isBlank()
                ? "career-pilot-dev-secret-change-me"
                : jwtSecret;
        this.issuer = issuer == null || issuer.isBlank() ? "career-pilot" : issuer;
        this.expirationSeconds = expirationSeconds <= 0 ? 86400 : expirationSeconds;
    }

    public String issueToken(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户 ID 无效");
        }

        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + expirationSeconds;
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String claims = "{"
                + "\"iss\":" + jsonString(issuer) + ","
                + "\"sub\":\"" + userId + "\","
                + "\"iat\":" + issuedAt + ","
                + "\"exp\":" + expiresAt
                + "}";
        String unsignedToken = base64Url(header.getBytes(StandardCharsets.UTF_8))
                + "."
                + base64Url(claims.getBytes(StandardCharsets.UTF_8));
        return unsignedToken + "." + sign(unsignedToken);
    }

    public long expirationSeconds() {
        return expirationSeconds;
    }

    public Long currentUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("请先登录");
        }

        return parseUserId(header.substring("Bearer ".length()).trim());
    }

    public Long parseUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }

        String claims = decodePart(parts[1]);
        String tokenIssuer = claim(ISSUER_PATTERN, claims, "登录状态无效，请重新登录");
        if (!issuer.equals(tokenIssuer)) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }

        long expiresAt = longClaim(EXPIRES_AT_PATTERN, claims, "登录已过期，请重新登录");
        if (expiresAt < Instant.now().getEpochSecond()) {
            throw new UnauthorizedException("登录已过期，请重新登录");
        }

        long userId = longClaim(SUBJECT_PATTERN, claims, "登录状态无效，请重新登录");
        if (userId <= 0) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }
        return userId;
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return base64Url(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("JWT 签名失败", ex);
        }
    }

    private String decodePart(String value) {
        try {
            return new String(BASE64_URL_DECODER.decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }
    }

    private String claim(Pattern pattern, String claims, String errorMessage) {
        Matcher matcher = pattern.matcher(claims);
        if (!matcher.find()) {
            throw new UnauthorizedException(errorMessage);
        }
        return matcher.group(1);
    }

    private long longClaim(Pattern pattern, String claims, String errorMessage) {
        try {
            return Long.parseLong(claim(pattern, claims, errorMessage));
        } catch (NumberFormatException ex) {
            throw new UnauthorizedException(errorMessage);
        }
    }

    private String jsonString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private String base64Url(byte[] bytes) {
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }
}
