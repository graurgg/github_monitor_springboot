package com.example.githubmonitor.controller;

import com.example.githubmonitor.dto.PagedResponse;
import com.example.githubmonitor.dto.UserDto;
import com.example.githubmonitor.service.UserService;
import com.example.githubmonitor.dto.UpdateUserDto;
import com.example.githubmonitor.dto.CreateUserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // User (1) and Admin (3) can GET all users
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

    // User (1) and Admin (3) can GET a single user
    @PreAuthorize("hasAnyRole('1', '3')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Manager (2) and Admin (3) can DELETE
    @PreAuthorize("hasAnyRole('2', '3')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('3')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id, 
            @RequestBody UpdateUserDto dto) {
        
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    // Manager (2) and Admin (3) can POST (Create users)
    @PreAuthorize("hasAnyRole('2', '3')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto dto) {
        UserDto createdUser = userService.createUser(dto);
        // Returns a 201 Created status code along with the new user's data
        return ResponseEntity.status(201).body(createdUser); 
    }
}