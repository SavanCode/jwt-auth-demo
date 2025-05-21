package com.example.jwtauth.service;

import com.example.jwtauth.entity.User;
import com.example.jwtauth.repository.UserRepository;
import com.example.jwtauth.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Mock密码编码器
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // Mock用户仓库
        userRepository = mock(UserRepository.class);
        
        // 初始化用户服务
        userService = new UserServiceImpl(passwordEncoder, userRepository);

        // 准备测试用户数据
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setRoles(Arrays.asList("USER"));
    }

    @Test
    void whenRegisterUser_thenSuccess() {
        // 设置mock行为
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
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
        // 设置mock行为
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        
        // 查找用户
        User foundUser = userService.findByUsername("testUser");

        // 验证结果
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void whenLoadByUsername_thenSuccess() {
        // 设置mock行为
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        
        // 加载用户
        UserDetails userDetails = userService.loadUserByUsername("testUser");

        // 验证结果
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    void whenLoadByUsername_thenThrowsException() {
        // 设置mock行为
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Optional.empty());
        
        // 验证不存在的用户会抛出异常
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistentUser");
        });
    }

    @Test
    void whenExistsByUsername_thenReturnsTrue() {
        // 设置mock行为
        when(userRepository.existsByUsername("testUser")).thenReturn(true);
        
        // 验证用户存在
        assertTrue(userService.existsByUsername("testUser"));
    }

    @Test
    void whenExistsByUsername_thenReturnsFalse() {
        // 设置mock行为
        when(userRepository.existsByUsername("nonexistentUser")).thenReturn(false);
        
        // 验证用户不存在
        assertFalse(userService.existsByUsername("nonexistentUser"));
    }
} 