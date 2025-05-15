/**
 * UserService实现类详解：
 * 
 * 1. 核心功能：
 *    - 用户注册：加密密码，分配角色，存储用户信息
 *    - 用户查询：根据用户名查找用户
 *    - 用户认证：实现Spring Security的UserDetailsService接口
 * 
 * 2. 数据存储：
 *    - 使用ConcurrentHashMap在内存中存储用户信息
 *    - 使用AtomicLong生成唯一的用户ID
 *    - 注意：这是演示用途，实际应用应该使用数据库存储
 * 
 * 3. 安全特性：
 *    - 密码加密：使用PasswordEncoder加密存储密码
 *    - 线程安全：使用ConcurrentHashMap确保并发安全
 *    - 角色管理：支持用户角色的分配和管理
 * 
 * 4. 测试用户：
 *    - 管理员账号：username=admin, password=admin123
 *    - 普通用户：username=user, password=user123
 * 
 * 5. 主要方法：
 *    - registerUser：注册新用户，包含密码加密和角色分配
 *    - existsByUsername：检查用户名是否已存在
 *    - findByUsername：根据用户名查找用户
 *    - loadUserByUsername：Spring Security认证时加载用户信息
 */

package com.example.jwtauth.service.impl;

import com.example.jwtauth.entity.User;
import com.example.jwtauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UserService的实现类
 * 使用内存存储实现用户管理
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    
    // 使用ConcurrentHashMap存储用户信息，确保线程安全
    private final Map<String, User> users = new ConcurrentHashMap<>();
    // 用于生成用户ID
    private final AtomicLong userIdSequence = new AtomicLong(1);

    /**
     * 注册新用户
     */
    @Override
    public User registerUser(User user) {
        // 设置用户ID
        user.setId(userIdSequence.getAndIncrement());
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 如果没有设置角色，设置默认角色
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Collections.singletonList("USER"));
        }
        
        // 存储用户信息
        users.put(user.getUsername(), user);
        
        return user;
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    /**
     * 根据用户名查找用户
     */
    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }

    /**
     * Spring Security需要的方法，根据用户名加载用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    // 初始化一些测试用户
    public void initializeUsers() {
        // 创建管理员用户
        if (!existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // 实际使用时应该使用更强的密码
            admin.setEmail("admin@example.com");
            admin.setRoles(Collections.singletonList("ADMIN"));
            registerUser(admin);
        }

        // 创建普通用户
        if (!existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword("user123"); // 实际使用时应该使用更强的密码
            user.setEmail("user@example.com");
            user.setRoles(Collections.singletonList("USER"));
            registerUser(user);
        }
    }
} 