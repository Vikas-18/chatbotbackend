package com.hostelchatbot.hostelchatbot.services;

import com.hostelchatbot.hostelchatbot.DTO.AdminPassword;
import com.hostelchatbot.hostelchatbot.repository.AdminPasswordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class AdminService {
    private static final String ADMIN_PASSWORD_ID = "admin-password";

    private final AdminPasswordRepository adminPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminPasswordRepository adminPasswordRepository, PasswordEncoder passwordEncoder) {
        this.adminPasswordRepository = adminPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void setupPassword(String password) {
        AdminPassword existingPassword = adminPasswordRepository.findById(ADMIN_PASSWORD_ID).orElse(null);

        if (existingPassword != null && password != null
                && passwordEncoder.matches(password, existingPassword.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New password must be different");
        }
        savePassword(password);
    }

    public void changePassword(String password) {
        if (!adminPasswordRepository.existsById(ADMIN_PASSWORD_ID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin password has not been configured");
        }
        savePassword(password);
    }

    public void authenticate(String password) {
        AdminPassword adminPassword = adminPasswordRepository.findById(ADMIN_PASSWORD_ID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid admin password"));

        if (password == null || !passwordEncoder.matches(password, adminPassword.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin password");
        }
    }

    private void savePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        AdminPassword adminPassword = new AdminPassword();
        adminPassword.setId(ADMIN_PASSWORD_ID);
        adminPassword.setPasswordHash(passwordEncoder.encode(password));
        adminPassword.setUpdatedAt(Instant.now());
        adminPasswordRepository.save(adminPassword);
    }
}
