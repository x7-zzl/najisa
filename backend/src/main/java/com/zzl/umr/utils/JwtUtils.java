package com.zzl.umr.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * @author zhangzl
 * @description JWT工具类
 * @date 2026/01/21 14:50:34
 */
@Component
public class JwtUtils {
    // 秘钥
    private static String SECRET;

    // 有效期 24 小时
    private static final long EXPIRATION = 86400000L;

    @Value("${auth.jwt.secret}")
    public void setSecret(String secret) {
        JwtUtils.SECRET = secret;
    }

    /**
     * 生成令牌
     */
    public static String createToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("created", new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     */
    public static String getUsernameFromToken(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();

    }

    /**
     * 获取令牌剩余时间
     * @param token 令牌
     * @return 剩余时间（毫秒）
     */
    public static long getRemainingTime(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public static void main(String[] args) {
        // 生成符合 HS512 要求的密钥
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("新的密钥: " + base64Key);
    }
}