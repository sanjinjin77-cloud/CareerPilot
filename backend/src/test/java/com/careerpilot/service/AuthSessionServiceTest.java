package com.careerpilot.service;

import com.careerpilot.common.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthSessionServiceTest {

    @Test
    void issueTokenCanBeParsedBackToOriginalUser() {
        AuthSessionService service = new AuthSessionService("test-secret", "career-pilot-test", 60);

        String token = service.issueToken(42L);

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(service.parseUserId(token)).isEqualTo(42L);
    }

    @Test
    void tamperedTokenIsRejected() {
        AuthSessionService service = new AuthSessionService("test-secret", "career-pilot-test", 60);
        String token = service.issueToken(42L);
        String tampered = token.replaceFirst(".$", token.endsWith("a") ? "b" : "a");

        assertThatThrownBy(() -> service.parseUserId(tampered))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("登录状态无效");
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        AuthSessionService service = new AuthSessionService("test-secret", "career-pilot-test", 1);
        String token = service.issueToken(42L);

        Thread.sleep(2200L);

        assertThatThrownBy(() -> service.parseUserId(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("登录已过期");
    }

    @Test
    void requestWithoutBearerTokenIsRejected() {
        AuthSessionService service = new AuthSessionService("test-secret", "career-pilot-test", 60);
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThatThrownBy(() -> service.currentUserId(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("请先登录");
    }

    @Test
    void requestWithBearerTokenResolvesCurrentUser() {
        AuthSessionService service = new AuthSessionService("test-secret", "career-pilot-test", 60);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + service.issueToken(7L));

        assertThat(service.currentUserId(request)).isEqualTo(7L);
    }
}
