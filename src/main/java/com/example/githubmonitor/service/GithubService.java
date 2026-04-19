package com.example.githubmonitor.service;

import com.example.githubmonitor.dto.IssueDto;
import com.example.githubmonitor.dto.RepoDto;
import com.example.githubmonitor.entity.GithubIssue;
import com.example.githubmonitor.entity.GithubRepo;
import com.example.githubmonitor.repository.GithubIssueRepository;
import com.example.githubmonitor.repository.GithubRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubRepoRepository repoRepository;
    private final GithubIssueRepository issueRepository;

    // Fix for line 41: change parameter to String and call findByGithubProfileUsername
    public List<RepoDto> getReposByUsername(String username) {
        return repoRepository.findByGithubProfileUsername(username)
                .stream()
                .map(repo -> new RepoDto(repo.getId(), repo.getName(), repo.getUrl()))
                .collect(Collectors.toList());
    }

    // Fix for line 67: change parameter to String and call findAllIssuesByUsername
    public Page<IssueDto> getIssuesByUsername(String username, Pageable pageable) {
        Page<GithubIssue> issuesPage = issueRepository.findAllIssuesByUsername(username, pageable);
        
        return issuesPage.map(issue -> new IssueDto(
                issue.getId(), 
                issue.getTitle(), 
                issue.getStatus(), 
                issue.getGithubRepo().getName()
        ));
    }
}