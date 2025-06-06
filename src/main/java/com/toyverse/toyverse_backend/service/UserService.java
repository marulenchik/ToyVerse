package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.RegisterRequest;
import com.toyverse.toyverse_backend.entity.User;

public interface UserService {

    User registerUser(RegisterRequest request);

    boolean authenticateUser(String username, String password);
}