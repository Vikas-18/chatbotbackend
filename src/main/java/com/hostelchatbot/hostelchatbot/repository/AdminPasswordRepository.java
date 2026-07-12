package com.hostelchatbot.hostelchatbot.repository;

import com.hostelchatbot.hostelchatbot.DTO.AdminPassword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPasswordRepository extends MongoRepository<AdminPassword, String> {
}
