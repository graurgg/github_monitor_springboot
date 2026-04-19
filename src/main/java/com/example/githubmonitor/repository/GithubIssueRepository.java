package com.example.githubmonitor.repository;

import com.example.githubmonitor.entity.GithubIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubIssueRepository extends JpaRepository<GithubIssue, Long> {
    
    // Updated to handle fetching by GitHub username
    @Query("SELECT i FROM GithubIssue i JOIN i.githubRepo r JOIN r.githubProfile p WHERE p.username = :username")
    Page<GithubIssue> findAllIssuesByUsername(@Param("username") String username, Pageable pageable);
}