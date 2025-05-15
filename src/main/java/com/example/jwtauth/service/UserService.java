package com.example.jwtauth.service;

import com.example.jwtauth.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务接口
 * 继承UserDetailsService以支持Spring Security的用户认证
 */
public interface UserService extends UserDetailsService {
    
    /**
     * 注册新用户
     * @param user 用户信息
     * @return 注册成功的用户
     */
    User registerUser(User user);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return true如果用户名已存在，否则false
     */
    boolean existsByUsername(String username);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
} 