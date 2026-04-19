package com.example.githubmonitor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // The inverse side of the Many-to-Many relationship
    @ManyToMany(mappedBy = "tags")
    private List<GithubRepo> repositories = new ArrayList<>();
}