package com.example.jwtauth.controller;

import com.example.jwtauth.dto.AuthRequest;
import com.example.jwtauth.dto.AuthResponse;
import com.example.jwtauth.entity.User;
import com.example.jwtauth.service.UserService;
import com.example.jwtauth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录和注册请求
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * 用户登录
     * @param authRequest 登录请求，包含用户名和密码
     * @return JWT token和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // 验证用户名和密码
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
            )
        );

        // 设置认证信息到Spring Security上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取认证用户信息
        User user = (User) authentication.getPrincipal();

        // 生成JWT token
        String token = jwtUtil.generateToken(user);

        // 构建响应
        AuthResponse response = new AuthResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRoles()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body("Error: Username is already taken!");
        }

        // 注册新用户
        User registeredUser = userService.registerUser(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * 获取当前登录用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user);
    }
} 