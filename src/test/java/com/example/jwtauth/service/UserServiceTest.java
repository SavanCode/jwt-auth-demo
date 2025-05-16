package com.example.jwtauth.service;

import com.example.jwtauth.entity.User;
import com.example.jwtauth.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Mock密码编码器
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // 初始化用户服务
        userService = new UserServiceImpl(passwordEncoder);

        // 准备测试用户数据
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setRoles(Arrays.asList("USER"));
    }

    @Test
    void whenRegisterUser_thenSuccess() {
        // 注册用户
        User registeredUser = userService.registerUser(testUser);

        // 验证结果
        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertTrue(registeredUser.getRoles().contains("USER"));
    }

    @Test
    void whenFindByUsername_thenSuccess() {
        // 先注册用户
        userService.registerUser(testUser);

        // 查找用户
        User foundUser = userService.findByUsername("testUser");

        // 验证结果
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void whenLoadByUsername_thenSuccess() {
        // 先注册用户
        userService.registerUser(testUser);

        // 加载用户
        UserDetails userDetails = userService.loadUserByUsername("testUser");

        // 验证结果
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    void whenLoadByUsername_thenThrowsException() {
        // 验证不存在的用户会抛出异常
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistentUser");
        });
    }

    @Test
    void whenExistsByUsername_thenReturnsTrue() {
        // 先注册用户
        userService.registerUser(testUser);

        // 验证用户存在
        assertTrue(userService.existsByUsername("testUser"));
    }

    @Test
    void whenExistsByUsername_thenReturnsFalse() {
        // 验证用户不存在
        assertFalse(userService.existsByUsername("nonexistentUser"));
    }
} 