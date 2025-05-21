/**
 * JWT (JSON Web Token) 工作流程：
 * 1. 用户登录成功后，调用generateToken方法生成JWT
 * 2. JWT包含了用户信息和过期时间，并使用密钥进行签名
 * 3. 服务器将token返回给客户端
 * 4. 客户端在后续的请求中，在Header中携带这个token
 * 5. 服务器通过validateToken方法验证token的有效性
 * 6. 如果token有效，允许访问；如果无效或过期，拒绝请求
 */

package com.example.jwtauth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key signingKey;

    @PostConstruct
    public void init() {
        // Generate a secure key using Keys.secretKeyFor
        this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // 用于签名JWT的密钥
    private Key getSigningKey() {
        return signingKey;
    }

    // 从token中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从token中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从token中提取指定的claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析token获取所有的claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查token是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 为用户生成token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 这里可以添加额外的信息到token中
        return createToken(claims, userDetails.getUsername());
    }

    // 创建token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)                 // 设置自定义信息
                .setSubject(subject)              // 设置用户名
                .setIssuedAt(new Date())          // 设置token创建时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用HS256算法签名
                .compact();
    }

    // 验证token是否有效
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
} 