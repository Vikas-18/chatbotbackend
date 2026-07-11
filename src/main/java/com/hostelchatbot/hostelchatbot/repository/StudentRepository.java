package com.hostelchatbot.hostelchatbot.repository;

import com.hostelchatbot.hostelchatbot.DTO.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student,String> {

    Optional<Student> findByRollNo(String rollNo);

    boolean existsByRollNo(String rollNo);
}
