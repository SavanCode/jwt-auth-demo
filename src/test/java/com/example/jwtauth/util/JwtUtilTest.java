package com.example.jwtauth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 设置测试密钥和过期时间
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyWithMinimumLength32Chars");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        // 创建测试用户
        userDetails = User.withUsername("testUser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void whenGenerateToken_thenTokenIsValid() {
        // 生成token
        String token = jwtUtil.generateToken(userDetails);

        // 验证token不为空
        assertNotNull(token);
        // 验证token可以被验证
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void whenExtractUsername_thenUsernameMatches() {
        // 生成token
        String token = jwtUtil.generateToken(userDetails);

        // 提取用户名并验证
        String username = jwtUtil.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void whenTokenExpired_thenValidationFails() {
        // 设置一个已过期的过期时间
        ReflectionTestUtils.setField(jwtUtil, "expiration", -3600000L);

        // 生成token（已过期）
        String token = jwtUtil.generateToken(userDetails);

        // 验证token已过期
        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void whenTokenForDifferentUser_thenValidationFails() {
        // 生成token
        String token = jwtUtil.generateToken(userDetails);

        // 创建不同的用户
        UserDetails differentUser = User.withUsername("differentUser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // 验证token对不同用户无效
        assertFalse(jwtUtil.validateToken(token, differentUser));
    }
} 