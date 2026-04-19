package com.example.githubmonitor.dto;

public record UserDto(
    Long id,
    String username,
    Integer roleId
) {}