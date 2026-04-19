package com.example.githubmonitor.service;

import com.example.githubmonitor.entity.GithubIssue;
import com.example.githubmonitor.entity.GithubProfile;
import com.example.githubmonitor.entity.GithubRepo;
import com.example.githubmonitor.repository.GithubProfileRepository;
import com.example.githubmonitor.repository.GithubRepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubSyncService {

    private final GithubProfileRepository profileRepository;

    @Value("${github.token}")
    private String githubToken;

    @Async
    @Transactional
    public void syncAllProfiles() {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
            List<GithubProfile> profiles = profileRepository.findAll();

            for (GithubProfile profile : profiles) {
                // Wrap in try-catch to skip invalid users
                try {
                    log.info("Syncing data for user: {}", profile.getUsername());
                    profile.getRepositories().clear();
                    
                    var ghUser = github.getUser(profile.getUsername());
                    
                    for (GHRepository ghRepo : ghUser.getRepositories().values()) {
                        GithubRepo repo = GithubRepo.builder()
                                .name(ghRepo.getName())
                                .url(ghRepo.getHtmlUrl().toString())
                                .githubProfile(profile)
                                .build();

                        List<GHIssue> ghIssues = ghRepo.queryIssues()
                                .state(org.kohsuke.github.GHIssueState.OPEN)
                                .list()
                                .iterator()
                                .nextPage();
                        for (GHIssue ghIssue : ghIssues) {
                            if (ghIssue.isPullRequest()) continue;

                            GithubIssue issue = GithubIssue.builder()
                                    .title(ghIssue.getTitle())
                                    .status(ghIssue.getState().name())
                                    .githubRepo(repo)
                                    .build();
                            
                            repo.getIssues().add(issue);
                        }
                        profile.getRepositories().add(repo);
                    }
                    profileRepository.save(profile);
                } catch (Exception e) {
                    // Log the error and move to the next profile
                    log.error("Skipped user '{}' due to error: {}", profile.getUsername(), e.getMessage());
                }
            }
            log.info("GitHub sync completed successfully!");

        } catch (IOException e) {
            log.error("Fatal error communicating with GitHub API", e);
            throw new RuntimeException("Failed to sync GitHub data", e);
        }
    }
}