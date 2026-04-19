package com.example.githubmonitor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepoDto(
    Long id,
    String name,
    @JsonAlias("html_url") String url // Reads 'html_url' from GitHub, outputs 'url' to client
) {}