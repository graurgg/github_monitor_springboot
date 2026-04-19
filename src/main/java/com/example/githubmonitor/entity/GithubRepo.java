package com.example.githubmonitor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "github_repos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String url;

    // N-to-1 Relationship back to Profile
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private GithubProfile githubProfile;

    // 1-to-N Relationship with Issues
    @OneToMany(mappedBy = "githubRepo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GithubIssue> issues = new ArrayList<>();

    // N-to-M Relationship with Tags
    @ManyToMany
    @JoinTable(
        name = "repo_tags",
        joinColumns = @JoinColumn(name = "repo_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();
}