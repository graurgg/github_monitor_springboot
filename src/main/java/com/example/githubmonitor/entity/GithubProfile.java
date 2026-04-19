package com.example.githubmonitor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "github_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    // The owning side of the 1-to-1 relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser appUser;

    // 1-to-N Relationship with Repositories
    @OneToMany(mappedBy = "githubProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GithubRepo> repositories = new ArrayList<>();
}