package com.example.githubmonitor.controller;

import com.example.githubmonitor.dto.IssueDto;
import com.example.githubmonitor.dto.RepoDto;
import com.example.githubmonitor.entity.GithubIssue;
import com.example.githubmonitor.entity.GithubRepo;
import com.example.githubmonitor.repository.GithubIssueRepository;
import com.example.githubmonitor.repository.GithubRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubRepoRepository repoRepository;
    private final GithubIssueRepository issueRepository;
    
    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/repos")
    public ResponseEntity<List<RepoDto>> getAllRepos() {
        List<RepoDto> repos = repoRepository.findAll().stream()
                .map(this::mapToRepoDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repos);
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/repos/{username}")
    public ResponseEntity<List<RepoDto>> getReposByUsername(@PathVariable String username) {
        List<RepoDto> repos = repoRepository.findByGithubProfileUsername(username).stream()
                .map(this::mapToRepoDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(repos);
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/issues")
    public ResponseEntity<List<IssueDto>> getAllIssues() {
        List<IssueDto> issues = issueRepository.findAll().stream()
                .map(this::mapToIssueDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(issues);
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/issues/{username}")
    public ResponseEntity<Page<IssueDto>> getIssuesByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<GithubIssue> issuesPage = issueRepository.findAllIssuesByUsername(username, PageRequest.of(page, size));
        return ResponseEntity.ok(issuesPage.map(this::mapToIssueDto));
    }

    // Helper mapping methods to prevent recursion
    private RepoDto mapToRepoDto(GithubRepo repo) {
        return new RepoDto(repo.getId(), repo.getName(), repo.getUrl());
    }

    private IssueDto mapToIssueDto(GithubIssue issue) {
        return new IssueDto(
                issue.getId(), 
                issue.getTitle(), 
                issue.getStatus(), 
                issue.getGithubRepo().getName()
        );
    }
}