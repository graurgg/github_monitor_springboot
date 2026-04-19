package com.example.githubmonitor.controller;

import com.example.githubmonitor.service.GithubSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final GithubSyncService githubSyncService;

    // Manager (2) and Admin (3) can SYNC
    @PreAuthorize("hasAnyRole('2', '3')")
    @PostMapping
    public ResponseEntity<String> triggerSync() {
        try {
            githubSyncService.syncAllProfiles();
            return ResponseEntity.ok("GitHub Sync Completed Successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("GitHub Sync Failed: " + e.getMessage());
        }
    }
}