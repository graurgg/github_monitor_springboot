package com.example.githubmonitor.repository;

import com.example.githubmonitor.entity.GithubProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GithubProfileRepository extends JpaRepository<GithubProfile, Long> {
    Optional<GithubProfile> findByAppUserId(Long userId);
    
    // Add this to check for duplicate GitHub usernames
    boolean existsByUsername(String username);
}