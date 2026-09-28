package com.careerpilot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.careerpilot.common.UnauthorizedException;
import com.careerpilot.entity.UserAccount;
import com.careerpilot.mapper.UserAccountMapper;
import com.careerpilot.service.AuthSessionService;
import com.careerpilot.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountMapper userAccountMapper;
    private final AuthSessionService authSessionService;
    private final PasswordService passwordService;

    public AuthController(UserAccountMapper userAccountMapper,
                          AuthSessionService authSessionService,
                          PasswordService passwordService) {
        this.userAccountMapper = userAccountMapper;
        this.authSessionService = authSessionService;
        this.passwordService = passwordService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> payload) {
        String account = required(payload.get("account"), "account");
        String password = required(payload.get("password"), "password");

        UserAccount user = userAccountMapper.selectOne(new LambdaQueryWrapper<UserAccount>()
                .and(wrapper -> wrapper.eq(UserAccount::getUsername, account)
                        .or()
                        .eq(UserAccount::getEmail, account))
                .last("LIMIT 1"));
        if (user == null || !passwordService.matches(password, user.getPasswordHash(), user.getSalt())) {
            throw new UnauthorizedException("账号或密码错误");
        }
        if (!"active".equals(user.getStatus())) {
            throw new UnauthorizedException("账号已被禁用");
        }

        upgradePasswordIfNeeded(user, password);
        return tokenResponse(user);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> payload) {
        String username = required(payload.get("username"), "username");
        String email = required(payload.get("email"), "email");
        String password = required(payload.get("password"), "password");
        if (password.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于 6 位");
        }

        Long count = userAccountMapper.selectCount(new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getUsername, username)
                .or()
                .eq(UserAccount::getEmail, email));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("用户名或邮箱已存在");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setEmail(email);
        user.setSalt(PasswordService.BCRYPT_SALT_MARKER);
        user.setPasswordHash(passwordService.hashPassword(password));
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userAccountMapper.insert(user);
        return tokenResponse(user);
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        Long userId = authSessionService.currentUserId(request);
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new UnauthorizedException("当前用户不存在，请重新登录");
        }
        return user(user);
    }

    private void upgradePasswordIfNeeded(UserAccount user, String rawPassword) {
        if (!passwordService.needsUpgrade(user.getPasswordHash())) {
            return;
        }
        user.setPasswordHash(passwordService.hashPassword(rawPassword));
        user.setSalt(PasswordService.BCRYPT_SALT_MARKER);
        user.setUpdatedAt(LocalDateTime.now());
        userAccountMapper.updateById(user);
    }

    private Map<String, Object> tokenResponse(UserAccount user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", authSessionService.issueToken(user.getId()));
        response.put("token_type", "bearer");
        response.put("expires_in", authSessionService.expirationSeconds());
        response.put("user", user(user));
        return response;
    }

    private Map<String, Object> user(UserAccount account) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", account.getId());
        user.put("username", account.getUsername());
        user.put("email", account.getEmail());
        return user;
    }

    private String required(Object value, String field) {
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
        return value.toString().trim();
    }
}
