package com.example.githubmonitor.service;

import com.example.githubmonitor.dto.IssueDto;
import com.example.githubmonitor.dto.RepoDto;
import com.example.githubmonitor.entity.AppUser;
import com.example.githubmonitor.entity.GithubIssue;
import com.example.githubmonitor.entity.GithubProfile;
import com.example.githubmonitor.entity.GithubRepo;
import com.example.githubmonitor.exception.AppException;
import com.example.githubmonitor.repository.AppUserRepository;
import com.example.githubmonitor.repository.GithubProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final AppUserRepository userRepository;
    private final GithubProfileRepository profileRepository;
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader("User-Agent", "GitHub-Monitor-App") // GitHub requires this!
            .build();

    @Transactional
    public void syncGithubDataForUser(Long userId, String githubUsername) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));

        GithubProfile profile = profileRepository.findByAppUserId(userId)
                .orElseGet(() -> {
                    GithubProfile newProfile = new GithubProfile();
                    newProfile.setAppUser(user);
                    return newProfile;
                });
        
        profile.setUsername(githubUsername);
        profile.getRepositories().clear();

        // 1. Fetch using your original RepoDto!
        List<RepoDto> apiRepos = fetchReposFromGithub(githubUsername);

        if (apiRepos != null) {
            for (RepoDto apiRepo : apiRepos) {
                GithubRepo repo = new GithubRepo();
                repo.setName(apiRepo.name());
                repo.setUrl(apiRepo.url()); // Maps cleanly because of @JsonAlias
                repo.setGithubProfile(profile);

                // 2. Fetch using your original IssueDto!
                List<IssueDto> apiIssues = fetchIssuesFromGithub(githubUsername, apiRepo.name());
                
                if (apiIssues != null) {
                    for (IssueDto apiIssue : apiIssues) {
                        GithubIssue issue = new GithubIssue();
                        issue.setTitle(apiIssue.title());
                        issue.setStatus(apiIssue.status()); // Maps cleanly because of @JsonAlias
                        issue.setGithubRepo(repo);
                        repo.getIssues().add(issue);
                    }
                }
                profile.getRepositories().add(repo);
            }
        }

        profileRepository.save(profile);
    }

    private List<RepoDto> fetchReposFromGithub(String username) {
        try {
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    // 1. Explicitly define List<RepoDto> here!
                    .body(new ParameterizedTypeReference<List<RepoDto>>() {}); 
        } catch (Exception e) {
            System.err.println("Failed to fetch repos for " + username + " -> " + e.getMessage());
            return List.of();
        }
    }

    private List<IssueDto> fetchIssuesFromGithub(String username, String repoName) {
        try {
            return restClient.get()
                    .uri("/repos/{username}/{repo}/issues", username, repoName)
                    .retrieve()
                    // 2. Explicitly define List<IssueDto> here!
                    .body(new ParameterizedTypeReference<List<IssueDto>>() {}); 
        } catch (Exception e) {
            System.err.println("Failed to fetch issues for repo " + repoName + " -> " + e.getMessage());
            return List.of();
        }
    }
}