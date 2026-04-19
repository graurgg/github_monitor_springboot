package com.example.githubmonitor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueDto(
    Long id,
    String title,
    @JsonAlias("state") String status, // Reads 'state' from GitHub, outputs 'status' to client
    String repoName
) {}