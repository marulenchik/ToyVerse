package com.toyverse.toyverse_backend.constant;

public class SecurityConstant {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ACCESS_DENIED_MESSAGE = "You are not allowed to access this page";
    public static final String FORBIDDEN_MESSAGE = "Login is needed to access this page";
    public static final String[] PUBLIC_URLS = {
            "/", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**",
            "/api/auth/**", "/toy-detail", "/api/public/**",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/", "/toys/**", "/admin", "/profile"
    };
}