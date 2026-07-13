package com.hostelchatbot.hostelchatbot.services;

import com.hostelchatbot.hostelchatbot.DTO.AdminPassword;
import com.hostelchatbot.hostelchatbot.repository.AdminPasswordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class AdminService {
    private final AdminPasswordRepository adminPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminPasswordRepository adminPasswordRepository, PasswordEncoder passwordEncoder) {
        this.adminPasswordRepository = adminPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void setupPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        List<AdminPassword> existingPasswords = adminPasswordRepository.findAll();
        boolean alreadyExists = existingPasswords.stream()
                .anyMatch(stored -> passwordEncoder.matches(password, stored.getPasswordHash()));

        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New password must be different");
        }

        savePassword(password);
    }

    public void authenticate(String password) {
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin password");
        }

        List<AdminPassword> existingPasswords = adminPasswordRepository.findAll();
        boolean valid = existingPasswords.stream()
                .anyMatch(stored -> passwordEncoder.matches(password, stored.getPasswordHash()));

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin password");
        }
    }

    private void savePassword(String password) {
        AdminPassword adminPassword = new AdminPassword();
        adminPassword.setPasswordHash(passwordEncoder.encode(password));
        adminPassword.setUpdatedAt(Instant.now());
        adminPasswordRepository.save(adminPassword);
    }
}
