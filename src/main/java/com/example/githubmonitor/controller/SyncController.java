package com.example.githubmonitor.controller;

import com.example.githubmonitor.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    // Manager (2) and Admin (3) can force a manual re-sync
    @PreAuthorize("hasAnyRole('2', '3')")
    @PostMapping("/{userId}")
    public ResponseEntity<String> triggerSync(
            @PathVariable Long userId, 
            @RequestParam String githubUsername) {
        
        syncService.syncGithubDataForUser(userId, githubUsername);
        return ResponseEntity.ok("GitHub Sync Completed for user " + githubUsername);
    }
}