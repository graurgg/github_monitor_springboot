package com.example.githubmonitor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "github_issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String status;

    // N-to-1 Relationship back to Repo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id")
    private GithubRepo githubRepo;
}