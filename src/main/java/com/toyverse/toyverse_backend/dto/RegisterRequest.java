package com.toyverse.toyverse_backend.dto;

import com.toyverse.toyverse_backend.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    private Role role = Role.USER; // Default to USER role
}