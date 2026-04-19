package com.example.githubmonitor.dto;

public record UserDto(
    Long id,
    String email,
    Integer roleId
) {}