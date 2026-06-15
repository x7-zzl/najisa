package com.zzl.umr.config.filter;

import com.zzl.umr.service.RedisService;
import com.zzl.umr.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.zzl.umr.constants.RedisKeyFixConstant.USER_LOGIN_TOKEN_KEY_FIX;

/**
 * @author zhangzl
 * @description JWT 认证过滤器
 * @date 2026/01/21 15:07:56
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 获取 Header 中的 Authorization
        String authHeader = request.getHeader("Authorization");

        // 检查 Token 是否存在且以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 如果没有 Token，直接放行，交给 Spring Security 后续配置判断
            chain.doFilter(request, response);
            return;
        }

        // 截取真正的 Token 字符串
        String token = authHeader.substring(7);


        // 检查 Token 是否在黑名单中
        if (redisService.hasKey(USER_LOGIN_TOKEN_KEY_FIX + token)) {
            // Token 已失效
            chain.doFilter(request, response);
            return;
        }

        try {
            // 解析 Token
            String username = JwtUtils.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);


                // 校验通过，设置安全上下文
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 重要：解析失败（如 MalformedJwtException）也不要抛出异常导致程序崩溃
            // 打印个日志即可，然后放行
            logger.error("JWT Token 解析失败: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}