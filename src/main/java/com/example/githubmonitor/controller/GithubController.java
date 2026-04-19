package com.example.githubmonitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GithubController {

    // Only User (1) and Admin (3) can access repositories and issues
    
    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/repos")
    public ResponseEntity<List<String>> getAllRepos() {
        return ResponseEntity.ok(List.of("Repo1", "Repo2"));
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/repos/{userId}")
    public ResponseEntity<String> getReposByUser(@PathVariable Long userId) {
        return ResponseEntity.ok("Repos for user " + userId);
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/issues")
    public ResponseEntity<List<String>> getAllIssues() {
        return ResponseEntity.ok(List.of("Issue1", "Issue2"));
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/issues/{userId}")
    public ResponseEntity<String> getIssuesByUser(@PathVariable Long userId) {
        // This is where you would call your custom JPQL method from the Repository!
        return ResponseEntity.ok("Issues for user " + userId);
    }
}