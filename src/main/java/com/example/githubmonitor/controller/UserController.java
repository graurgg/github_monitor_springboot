package com.example.githubmonitor.controller;

import com.example.githubmonitor.dto.CreateUserDto;
import com.example.githubmonitor.dto.PagedResponse;
import com.example.githubmonitor.dto.UserDto;
import com.example.githubmonitor.service.GithubSyncService;
import com.example.githubmonitor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GithubSyncService githubSyncService;

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping
    public ResponseEntity<PagedResponse<UserDto>> getUsers(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('3')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto dto) {
        // 1. Create the user
        UserDto createdUser = userService.createUser(dto);
        
        // 2. Automatically sync all github profiles upon creation
        // Note: For a production app, consider making this async so the client doesn't wait
        githubSyncService.syncAllProfiles(); 
        
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('2', '3')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Removed the PUT mapping as requested
}