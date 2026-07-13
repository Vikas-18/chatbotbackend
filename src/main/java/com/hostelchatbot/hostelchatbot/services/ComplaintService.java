package com.hostelchatbot.hostelchatbot.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hostelchatbot.hostelchatbot.DTO.Complaint;
import com.hostelchatbot.hostelchatbot.repository.ComplaintRepository;

@Service
public class ComplaintService {
    ComplaintRepository complaintRepository;
    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }  
    
    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    //get all complaints
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    //get complaints by type
    public List<Complaint> getComplaintsByType(String type) {
        return complaintRepository.findByType(type);
    }

    //get complaints by resolution status
    public List<Complaint> getComplaintsByResolutionStatus(boolean resolved) {
        return complaintRepository.findByResolved(resolved);
    }

    //get complaints by hostel name
    public List<Complaint> getComplaintsByHostelName(String hostelName) {
        return complaintRepository.findByHostelName(hostelName);
    }

    //resolve complaint by id
    public Complaint resolveComplaint(String id) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setResolved(true);
            return complaintRepository.save(complaint);
        }
        return null;
    }
}
