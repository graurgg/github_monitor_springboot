package com.example.githubmonitor.repository;

import com.example.githubmonitor.entity.GithubRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {
    // Changed to fetch by GitHub username
    List<GithubRepo> findByGithubProfileUsername(String username);
}