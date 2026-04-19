package com.example.githubmonitor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    // 1-to-1 Relationship with GithubProfile
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GithubProfile githubProfile;
}