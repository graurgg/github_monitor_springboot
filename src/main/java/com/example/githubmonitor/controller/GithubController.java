package com.example.githubmonitor.controller;

import com.example.githubmonitor.dto.IssueDto;
import com.example.githubmonitor.dto.PagedResponse;
import com.example.githubmonitor.dto.RepoDto;
import com.example.githubmonitor.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    // Only User (1) and Admin (3) can access repositories and issues

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/repos")
    public ResponseEntity<PagedResponse<RepoDto>> getAllRepos(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(githubService.getAllRepos(page, size));
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/repos/{userId}")
    public ResponseEntity<List<RepoDto>> getReposByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(githubService.getReposByUserId(userId));
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/issues")
    public ResponseEntity<PagedResponse<IssueDto>> getAllIssues(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(githubService.getAllIssues(page, size));
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/issues/{userId}")
    public ResponseEntity<PagedResponse<IssueDto>> getIssuesByUser(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return ResponseEntity.ok(githubService.getIssuesByUserId(userId, page, size, sortBy, sortDir));
    }
}