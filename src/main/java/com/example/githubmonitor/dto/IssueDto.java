package com.example.githubmonitor.dto;

public record IssueDto(
    Long id,
    String title,
    String status,
    String repoName
) {}