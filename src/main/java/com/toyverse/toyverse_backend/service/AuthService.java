package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.LoginRequestDto;
import com.toyverse.toyverse_backend.dto.RegisterRequestDto;

public interface AuthService {
    void registerUser(RegisterRequestDto registerRequest);
    String login(LoginRequestDto loginRequest);
}
