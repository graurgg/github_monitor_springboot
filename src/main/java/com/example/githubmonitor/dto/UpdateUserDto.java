package com.example.githubmonitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDto(
    @NotBlank(message = "Password cannot be blank")
    String newPassword,
    
    @NotNull(message = "Role ID cannot be null")
    Integer roleId
) {}