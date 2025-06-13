package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.LoginRequestDto;
import com.toyverse.toyverse_backend.dto.RegisterRequestDto;
import com.toyverse.toyverse_backend.entity.Role;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.repository.UserRepository;
import com.toyverse.toyverse_backend.security.JwtTokenProvider;
import com.toyverse.toyverse_backend.service.implementation.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.USER);

        loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded_password");
        user.setRole(Role.USER);
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenUsernameNotExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        assertDoesNotThrow(() -> authService.registerUser(registerRequest));

        // Then
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(registerRequest)
        );

        assertEquals("Username is already taken!", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldSaveUserWithEncodedPassword() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        authService.registerUser(registerRequest);

        // Then
        verify(userRepository).save(argThat(savedUser -> 
            savedUser.getUsername().equals("testuser") &&
            savedUser.getPassword().equals("encoded_password") &&
            savedUser.getRole().equals(Role.USER)
        ));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        // Given
        String expectedToken = "jwt.token.here";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        // When
        String actualToken = authService.login(loginRequest);

        // Then
        assertEquals(expectedToken, actualToken);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void login_ShouldAuthenticateWithCorrectCredentials() {
        // Given
        String expectedToken = "jwt.token.here";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        // When
        authService.login(loginRequest);

        // Then
        verify(authenticationManager).authenticate(argThat(authToken -> 
            authToken.getPrincipal().equals("testuser") &&
            authToken.getCredentials().equals("password123")
        ));
    }

    @Test
    void registerUser_ShouldHandleDifferentRoles() {
        // Given
        RegisterRequestDto adminRequest = new RegisterRequestDto();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("admin123");
        adminRequest.setRole(Role.ADMIN);

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded_admin_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        authService.registerUser(adminRequest);

        // Then
        verify(userRepository).save(argThat(savedUser -> 
            savedUser.getUsername().equals("admin") &&
            savedUser.getRole().equals(Role.ADMIN)
        ));
    }

    @Test
    void registerUser_ShouldHandleNullValues() {
        // Given
        RegisterRequestDto nullRequest = new RegisterRequestDto();
        nullRequest.setUsername(null);
        nullRequest.setPassword(null);
        nullRequest.setRole(null);

        when(userRepository.existsByUsername(null)).thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> authService.registerUser(nullRequest));
        verify(userRepository).existsByUsername(null);
    }
} 