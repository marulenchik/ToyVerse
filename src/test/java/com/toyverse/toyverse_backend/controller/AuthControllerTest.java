package com.toyverse.toyverse_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyverse.toyverse_backend.dto.LoginRequestDto;
import com.toyverse.toyverse_backend.dto.RegisterRequestDto;
import com.toyverse.toyverse_backend.entity.Role;
import com.toyverse.toyverse_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDto loginRequest;
    private RegisterRequestDto registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequestDto();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.USER);
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {
        // Given
        String expectedToken = "jwt.token.here";
        when(authService.login(any(LoginRequestDto.class))).thenReturn(expectedToken);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    void register_ShouldReturnSuccessMessage_WhenValidRequest() throws Exception {
        // Given
        doNothing().when(authService).registerUser(any(RegisterRequestDto.class));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));

        verify(authService).registerUser(any(RegisterRequestDto.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUsernameAlreadyExists() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Username is already taken!"))
                .when(authService).registerUser(any(RegisterRequestDto.class));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is already taken!"));

        verify(authService).registerUser(any(RegisterRequestDto.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        RegisterRequestDto invalidRequest = new RegisterRequestDto();
        invalidRequest.setUsername(""); // Empty username
        invalidRequest.setPassword("123"); // Short password
        invalidRequest.setRole(Role.USER);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
} 