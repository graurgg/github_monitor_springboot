package com.example.githubmonitor.dto;

public record RepoDto(
    Long id,
    String name,
    String url
) {}