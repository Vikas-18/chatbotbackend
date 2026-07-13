package com.hostelchatbot.hostelchatbot.controllers;

import com.hostelchatbot.hostelchatbot.DTO.AdminLoginRequest;
import com.hostelchatbot.hostelchatbot.DTO.AdminPasswordRequest;
import com.hostelchatbot.hostelchatbot.DTO.Complaint;
import com.hostelchatbot.hostelchatbot.DTO.LoginResponse;
import com.hostelchatbot.hostelchatbot.services.AdminService;
import com.hostelchatbot.hostelchatbot.services.ComplaintService;
import com.hostelchatbot.hostelchatbot.services.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final JwtService jwtService;
    private final ComplaintService complaintService;

    public AdminController(AdminService adminService, JwtService jwtService, ComplaintService complaintService) {
        this.adminService = adminService;
        this.jwtService = jwtService;
        this.complaintService = complaintService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AdminLoginRequest loginRequest) {
        adminService.authenticate(loginRequest.password());
        return jwtService.createAdminToken();
    }

    @PostMapping("/setup-password")
    public String setupPassword(@RequestBody AdminPasswordRequest passwordRequest) {
        adminService.setupPassword(passwordRequest.password());
        return "Admin password configured";
    }

    //resolve complaint
    @PostMapping("/complaints/resolve")
    public Complaint resolveComplaint(@RequestBody String id) {
        return complaintService.resolveComplaint(id);
    }

}
