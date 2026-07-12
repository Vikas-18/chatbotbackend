package com.hostelchatbot.hostelchatbot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hostelchatbot.hostelchatbot.DTO.Complaint;
@Repository
public interface ComplaintRepository extends MongoRepository<Complaint, String> {

    List<Complaint> findByType(String type);
    List<Complaint> findByResolved(boolean resolved);

}
