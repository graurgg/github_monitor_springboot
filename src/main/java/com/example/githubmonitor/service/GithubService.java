package com.example.githubmonitor.service;

import com.example.githubmonitor.dto.IssueDto;
import com.example.githubmonitor.dto.PagedResponse;
import com.example.githubmonitor.dto.RepoDto;
import com.example.githubmonitor.entity.GithubIssue;
import com.example.githubmonitor.entity.GithubRepo;
import com.example.githubmonitor.repository.GithubIssueRepository;
import com.example.githubmonitor.repository.GithubRepoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubRepoRepository repoRepository;
    private final GithubIssueRepository issueRepository;

    // Fetch all repositories globally (Paginated)
    public PagedResponse<RepoDto> getAllRepos(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<GithubRepo> page = repoRepository.findAll(pageable);
        
        List<RepoDto> content = page.getContent().stream()
                .map(repo -> new RepoDto(repo.getId(), repo.getName(), repo.getUrl()))
                .collect(Collectors.toList());
                
        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    // Fetch repositories for a specific user ID
    public List<RepoDto> getReposByUserId(Long userId) {
        return repoRepository.findByGithubProfileAppUserId(userId).stream()
                .map(repo -> new RepoDto(repo.getId(), repo.getName(), repo.getUrl()))
                .collect(Collectors.toList());
    }

    // Fetch all issues globally (Paginated)
    public PagedResponse<IssueDto> getAllIssues(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<GithubIssue> page = issueRepository.findAll(pageable);
        
        List<IssueDto> content = page.getContent().stream()
                .map(issue -> new IssueDto(issue.getId(), issue.getTitle(), issue.getStatus(), issue.getGithubRepo().getName()))
                .collect(Collectors.toList());
                
        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    // Fetch issues for a specific user ID using our Custom JPQL (Paginated)
    public PagedResponse<IssueDto> getIssuesByUserId(Long userId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
                
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // This calls the custom @Query we wrote in Phase 4!
        Page<GithubIssue> page = issueRepository.findAllIssuesByUserId(userId, pageable);

        List<IssueDto> content = page.getContent().stream()
                .map(issue -> new IssueDto(issue.getId(), issue.getTitle(), issue.getStatus(), issue.getGithubRepo().getName()))
                .collect(Collectors.toList());

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}