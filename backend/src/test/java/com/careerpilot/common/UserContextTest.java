package com.careerpilot.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserContextTest {

    @Test
    void validUserIdIsReturnedForOwnedDataQueries() {
        assertThat(UserContext.requireValidUserId(12L)).isEqualTo(12L);
    }

    @Test
    void missingUserIdIsRejectedInsteadOfFallingBackToDemoUser() {
        assertThatThrownBy(() -> UserContext.requireValidUserId(null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("请先登录");
    }

    @Test
    void nonPositiveUserIdIsRejectedInsteadOfFallingBackToDemoUser() {
        assertThatThrownBy(() -> UserContext.requireValidUserId(0L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("请先登录");
    }
}
