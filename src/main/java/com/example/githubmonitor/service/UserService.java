package com.example.githubmonitor.service;

import com.example.githubmonitor.dto.PagedResponse;
import com.example.githubmonitor.dto.UserDto;
import com.example.githubmonitor.dto.UpdateUserDto;
import com.example.githubmonitor.entity.AppUser;
import com.example.githubmonitor.exception.AppException;
import com.example.githubmonitor.repository.AppUserRepository;
import com.example.githubmonitor.dto.CreateUserDto;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SyncService syncService; // Inject the new service!

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new AppException(org.springframework.http.HttpStatus.BAD_REQUEST, "EMAIL_TAKEN", "A user with this email already exists.");
        }

        AppUser newUser = new AppUser();
        newUser.setEmail(dto.email());
        newUser.setPassword(passwordEncoder.encode(dto.password())); // Always encrypt!
        newUser.setRoleId(dto.roleId());

        AppUser savedUser = userRepository.save(newUser);

        // Trigger the GitHub Sync automatically
        try {
            syncService.syncGithubDataForUser(savedUser.getId(), dto.githubUsername());
        } catch (Exception e) {
            System.err.println("User created, but GitHub sync failed: " + e.getMessage());
        }

        return new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getRoleId());
    }

    // Upgraded to handle Pagination and Sorting
    public PagedResponse<UserDto> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        
        // 1. Determine sort direction
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Create the Pageable object
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // 3. Fetch from database
        Page<AppUser> usersPage = userRepository.findAll(pageable);

        // 4. Map entities to DTOs
        List<UserDto> content = usersPage.getContent().stream()
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getRoleId()))
                .collect(Collectors.toList());

        // 5. Wrap in our custom response
        return new PagedResponse<>(
                content,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
    }

    public UserDto getUserById(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND, 
                        "USER_NOT_FOUND", 
                        "User with ID " + id + " does not exist."
                ));

        return new UserDto(user.getId(), user.getEmail(), user.getRoleId());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(
                    HttpStatus.NOT_FOUND, 
                    "USER_NOT_FOUND", 
                    "Cannot delete. User with ID " + id + " does not exist."
            );
        }
        userRepository.deleteById(id);
    }

    public UserDto updateUser(Long id, UpdateUserDto dto) {
        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND, 
                        "USER_NOT_FOUND", 
                        "User with ID " + id + " does not exist."
                ));

        // In a real app, you would hash the password here using PasswordEncoder
        existingUser.setPassword(dto.newPassword());
        existingUser.setRoleId(dto.roleId());

        AppUser savedUser = userRepository.save(existingUser);

        return new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getRoleId());
    }
}