package com.example.githubmonitor.service;

import com.example.githubmonitor.dto.CreateUserDto;
import com.example.githubmonitor.dto.PagedResponse;
import com.example.githubmonitor.dto.UserDto;
import com.example.githubmonitor.entity.AppUser;
import com.example.githubmonitor.entity.GithubProfile;
import com.example.githubmonitor.exception.AppException;
import com.example.githubmonitor.repository.AppUserRepository;
import com.example.githubmonitor.repository.GithubProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;
    private final GithubProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public PagedResponse<UserDto> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<AppUser> usersPage = userRepository.findAll(pageable);

        // Map AppUser to UserDto using the associated GithubProfile username
        List<UserDto> content = usersPage.getContent().stream()
                .map(user -> new UserDto(
                        user.getId(), 
                        user.getGithubProfile() != null ? user.getGithubProfile().getUsername() : "N/A", 
                        user.getRoleId()
                ))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content, usersPage.getNumber(), usersPage.getSize(),
                usersPage.getTotalElements(), usersPage.getTotalPages(), usersPage.isLast()
        );
    }

    public UserDto getUserById(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found."));
        
        return new UserDto(
                user.getId(), 
                user.getGithubProfile() != null ? user.getGithubProfile().getUsername() : "N/A", 
                user.getRoleId()
        );
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        // 1. Check for duplicate Email
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "EMAIL_IN_USE", "Email already registered.");
        }

        // 2. Check for duplicate GitHub Username
        if (profileRepository.existsByUsername(dto.githubUsername())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "GITHUB_USER_EXISTS", 
                "A user with GitHub username '" + dto.githubUsername() + "' is already registered.");
        }

        AppUser user = AppUser.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .roleId(dto.roleId())
                .build();

        AppUser savedUser = userRepository.save(user);

        GithubProfile profile = GithubProfile.builder()
                .username(dto.githubUsername())
                .appUser(savedUser)
                .build();
        
        profileRepository.save(profile);

        return new UserDto(savedUser.getId(), profile.getUsername(), savedUser.getRoleId());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User does not exist.");
        }
        userRepository.deleteById(id);
    }

}