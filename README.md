# Spring Boot JWT Authentication Implementation
# Spring Boot JWT 认证系统实现

This project implements a secure JWT (JSON Web Token) authentication system using Spring Boot.
本项目使用 Spring Boot 实现了一个安全的 JWT（JSON Web Token）认证系统。

## Architecture Overview 系统架构概览

```mermaid
graph TD
    A[JwtAuthenticationFilter] -->|Uses 使用| B[JwtUtil]
    A -->|Lazy Load 懒加载| C[UserService]
    D[SecurityConfig] -->|Lazy Load 懒加载| C
    D -->|Configures 配置| A
    C -->|Implements 实现| E[UserDetailsService]
    B -->|Token Operations 令牌操作| F[JWT Operations]
```

## Key Components 核心组件

### 1. JwtAuthenticationFilter (JWT认证过滤器)
- Extracts and validates JWT tokens from request headers
- Uses ApplicationContext for lazy loading of UserService to avoid circular dependencies
- Sets up SecurityContext with authenticated user information
- Implements token validation workflow

- 从请求头中提取并验证 JWT 令牌
- 使用 ApplicationContext 懒加载 UserService 以避免循环依赖
- 设置已认证用户信息到 SecurityContext
- 实现令牌验证流程

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Uses ApplicationContext for lazy loading
    // 使用 ApplicationContext 实现懒加载
    @Autowired
    private ApplicationContext applicationContext;
    
    // Handles token validation and authentication
    // Sets SecurityContext with authenticated user
    // 处理令牌验证和认证
    // 设置认证用户到 SecurityContext
}
```

### 2. SecurityConfig (安全配置)
- Configures Spring Security rules and authentication flow
- Implements CORS and session management
- Uses ApplicationContext to resolve circular dependencies
- Sets up security rules for public and protected endpoints

- 配置 Spring Security 规则和认证流程
- 实现 CORS 和会话管理
- 使用 ApplicationContext 解决循环依赖
- 设置公开和受保护端点的安全规则

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // Uses ApplicationContext for lazy loading
    // Configures security rules and authentication
    // Sets up CORS and session management
    
    // 使用 ApplicationContext 实现懒加载
    // 配置安全规则和认证
    // 设置 CORS 和会话管理
}
```

### 3. UserService (用户服务)
- Implements UserDetailsService interface
- Manages user authentication and data
- Loaded lazily to prevent circular dependencies

- 实现 UserDetailsService 接口
- 管理用户认证和数据
- 懒加载以防止循环依赖

### 4. JwtUtil (JWT工具类)
- Handles token generation and validation
- Manages token expiration and claims
- Operates independently without circular dependencies

- 处理令牌生成和验证
- 管理令牌过期和声明
- 独立运行，无循环依赖

## Security Features 安全特性

1. **Token Management 令牌管理**
   - Secure token generation and validation 安全的令牌生成和验证
   - Configurable token expiration 可配置的令牌过期时间
   - Claims-based token content 基于声明的令牌内容

2. **Authentication Flow 认证流程**
   - Stateless authentication using JWT 使用 JWT 的无状态认证
   - Bearer token authentication scheme Bearer 令牌认证方案
   - Secure password handling with BCrypt encryption 使用 BCrypt 加密处理密码

3. **Security Configurations 安全配置**
   - CORS support for cross-origin requests 支持跨域请求的 CORS
   - Stateless session management 无状态会话管理
   - Protected API endpoints 受保护的 API 端点
   - Public endpoints for authentication 用于认证的公开端点

## API Endpoints API 端点

### 1. Public Endpoints 公开端点
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "user1",
    "password": "password123",
    "email": "user1@example.com"
}
```

### 2. Protected Endpoints 受保护端点
需要在请求头中包含有效的 JWT 令牌：
```http
GET /api/protected-resource
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Project Structure 项目结构

```
jwt-auth-demo/
├── src/main/java/com/example/jwtauth/
│   ├── config/
│   │   └── SecurityConfig.java           # Spring Security配置
│   ├── controller/
│   │   └── AuthController.java          # 认证控制器
│   ├── dto/
│   │   ├── AuthRequest.java             # 认证请求DTO
│   │   └── AuthResponse.java            # 认证响应DTO
│   ├── entity/
│   │   └── User.java                    # 用户实体
│   ├── security/
│   │   └── JwtAuthenticationFilter.java  # JWT认证过滤器
│   ├── service/
│   │   ├── UserService.java             # 用户服务接口
│   │   └── impl/
│   │       └── UserServiceImpl.java     # 用户服务实现
│   ├── util/
│   │   └── JwtUtil.java                 # JWT工具类
│   └── JwtAuthApplication.java          # 应用程序入口
└── pom.xml                              # Maven配置文件
```

## Getting Started 快速开始

1. Configure application.yml with your settings
   配置 application.yml 设置

2. Set up your database configuration
   设置数据库配置

3. Run the application
   运行应用程序：
   ```bash
   mvn spring-boot:run
   ```

4. Test the API endpoints
   测试 API 端点：
   ```bash
   # Register a new user 注册新用户
   curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" \
   -d '{"username":"user1","password":"password123","email":"user1@example.com"}'

   # Login to get token 登录获取令牌
   curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
   -d '{"username":"user1","password":"password123"}'

   # Access protected resource 访问受保护资源
   curl -X GET http://localhost:8080/api/protected-resource -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

## Production Considerations 生产环境注意事项

1. **Security 安全性**
   - Use HTTPS in production 在生产环境中使用 HTTPS
   - Implement rate limiting 实现速率限制
   - Configure appropriate CORS policies 配置适当的 CORS 策略
   - Regularly rotate security keys 定期轮换安全密钥

2. **Performance 性能**
   - Implement caching where appropriate 适当实现缓存
   - Monitor token validation performance 监控令牌验证性能
   - Consider token blacklisting for revocation 考虑使用令牌黑名单进行撤销

3. **Maintenance 维护**
   - Regular security updates 定期安全更新
   - Token expiration policy reviews 令牌过期策略审查
   - Audit logging implementation 审计日志实现

## Notes 注意事项

- The implementation uses lazy loading through ApplicationContext to resolve circular dependencies
  实现通过 ApplicationContext 使用懒加载来解决循环依赖
- Security configurations are customizable through application.yml
  安全配置可通过 application.yml 自定义
- Token expiration and other JWT settings can be configured as needed
  令牌过期时间和其他 JWT 设置可根据需要配置

## 项目结构

```
jwt-auth-demo/
├── src/main/java/com/example/jwtauth/
│   ├── config/
│   │   └── SecurityConfig.java           # Spring Security配置
│   ├── controller/
│   │   └── AuthController.java          # 认证控制器
│   ├── dto/
│   │   ├── AuthRequest.java             # 认证请求DTO
│   │   └── AuthResponse.java            # 认证响应DTO
│   ├── entity/
│   │   └── User.java                    # 用户实体
│   ├── security/
│   │   └── JwtAuthenticationFilter.java  # JWT认证过滤器
│   ├── service/
│   │   ├── UserService.java             # 用户服务接口
│   │   └── impl/
│   │       └── UserServiceImpl.java     # 用户服务实现
│   ├── util/
│   │   └── JwtUtil.java                 # JWT工具类
│   └── JwtAuthApplication.java          # 应用程序入口
└── pom.xml                              # Maven配置文件
```

## JWT认证流程

### 1. 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "user1",
    "password": "password123",
    "email": "user1@example.com"
}
```
- 检查用户名是否已存在
- 对密码进行加密
- 保存用户信息
- 返回注册成功消息

### 2. 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "user1",
    "password": "password123"
}
```
流程：
1. 验证用户名和密码
2. 生成JWT token
3. 返回token和用户信息
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "user1",
    "email": "user1@example.com",
    "roles": ["USER"]
}
```

### 3. 访问受保护资源
```http
GET /api/protected-resource
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
认证过程：
1. JWT过滤器提取token
2. 验证token签名
3. 检查token是否过期
4. 加载用户信息
5. 设置安全上下文
6. 允许/拒绝访问资源

## JWT Token结构

JWT token包含三部分：
1. Header（头部）
   ```json
   {
     "alg": "HS256",
     "typ": "JWT"
   }
   ```

2. Payload（负载）
   ```json
   {
     "sub": "user1",         // 用户名
     "iat": 1516239022,     // 签发时间
     "exp": 1516242622      // 过期时间
   }
   ```

3. Signature（签名）
   - 使用密钥对头部和负载进行签名
   - 确保数据不被篡改

## 安全特性

1. 密码安全
   - 使用BCrypt加密存储密码
   - 密码从不以明文传输或存储

2. Token安全
   - token有效期限制（默认24小时）
   - 使用HS256算法签名
   - 无状态认证，不存储session

3. 请求安全
   - 启用CORS支持跨域请求
   - 禁用CSRF（使用token认证不需要）
   - 所有非公开API都需要认证

## 快速开始

1. 克隆项目
2. 修改`application.yml`中的JWT密钥
3. 运行项目：
   ```bash
   mvn spring-boot:run
   ```

4. 测试API：
   ```bash
   # 注册用户
   curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"username":"user1","password":"password123","email":"user1@example.com"}'

   # 登录获取token
   curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"user1","password":"password123"}'

   # 使用token访问受保护资源
   curl -X GET http://localhost:8080/api/auth/me -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

## 注意事项

1. 生产环境使用建议：
   - 使用更强的密钥
   - 配置适当的token过期时间
   - 实现token刷新机制
   - 使用HTTPS传输
   - 实现用户注销机制

2. 安全提醒：
   - 定期更换JWT密钥
   - 监控异常登录
   - 实现请求频率限制
   - 记录安全审计日志 