package com.example.githubmonitor.config;

import com.example.githubmonitor.entity.AppUser;
import com.example.githubmonitor.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@default.com";
        String adminPassword = "default";

        // Check if the user exists
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            AppUser admin = new AppUser();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword)); // Encrypt perfectly
            admin.setRoleId(3);
            
            userRepository.save(admin);
            System.out.println("Default admin user seeded into the database.");
        } else {
            // If the user exists but the password hash was wrong, force update it
            AppUser existingAdmin = userRepository.findByEmail(adminEmail).get();
            existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
            
            userRepository.save(existingAdmin);
            System.out.println("Default admin password forcibly reset to 'default'.");
        }
    }
}