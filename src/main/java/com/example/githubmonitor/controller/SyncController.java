package com.example.githubmonitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
public class SyncController {

    // Manager (2) and Admin (3) can SYNC
    @PreAuthorize("hasAnyRole('2', '3')")
    @PostMapping
    public ResponseEntity<String> triggerSync() {
        return ResponseEntity.ok("GitHub Sync Initiated!");
    }
}