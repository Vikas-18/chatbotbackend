package com.hostelchatbot.hostelchatbot.controllers;

import com.hostelchatbot.hostelchatbot.DTO.Complaint;
import com.hostelchatbot.hostelchatbot.DTO.ComplaintCreateRequest;
import com.hostelchatbot.hostelchatbot.DTO.Student;
import com.hostelchatbot.hostelchatbot.services.ComplaintService;
import com.hostelchatbot.hostelchatbot.services.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ComplaintController {
    private final ComplaintService complaintService;
    private final StudentService studentService;

    public ComplaintController(ComplaintService complaintService, StudentService studentService) {
        this.complaintService = complaintService;
        this.studentService = studentService;
    }

    @GetMapping("/complaints")
    public List<Complaint> getComplaints() {
        return complaintService.getAllComplaints();
    }

    @PostMapping("/complaints")
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        return complaintService.saveComplaint(complaint);
    }

    @PostMapping("/complaints/ai")
    public Complaint createComplaintFromAi(@RequestBody ComplaintCreateRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is required");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        String rollNo = authentication.getName();
        if (rollNo == null || rollNo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Student student = studentService.findByRollNo(rollNo);
        return complaintService.createComplaintFromMessage(request.message(), student);
    }

    @GetMapping("/complaints/type/{type}")
    public List<Complaint> getComplaintsByType(@PathVariable String type) {
        return complaintService.getComplaintsByType(type);
    }

    @GetMapping("/complaints/resolved/{resolved}")
    public List<Complaint> getComplaintsByResolutionStatus(@PathVariable boolean resolved) {
        return complaintService.getComplaintsByResolutionStatus(resolved);
    }

    @GetMapping("/complaints/hostel/{hostelName}")
    public List<Complaint> getComplaintsByHostelName(@PathVariable String hostelName) {
        return complaintService.getComplaintsByHostelName(hostelName);
    }
}
