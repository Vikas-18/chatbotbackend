package com.hostelchatbot.hostelchatbot.controllers;

import com.hostelchatbot.hostelchatbot.DTO.Complaint;
import com.hostelchatbot.hostelchatbot.services.ComplaintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ComplaintController {
    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    public List<Complaint> getComplaints() {
        return complaintService.getAllComplaints();
    }

    @PostMapping("/complaints")
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        return complaintService.saveComplaint(complaint);
    }

    @GetMapping("/complaints/type/{type}")
    public List<Complaint> getComplaintsByType(@PathVariable String type) {
        return complaintService.getComplaintsByType(type);
    }

    @GetMapping("/complaints/resolved/{resolved}")
    public List<Complaint> getComplaintsByResolutionStatus(@PathVariable boolean resolved) {
        return complaintService.getComplaintsByResolutionStatus(resolved);
    }
}
