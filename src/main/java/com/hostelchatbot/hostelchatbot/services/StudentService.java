package com.hostelchatbot.hostelchatbot.services;

import com.hostelchatbot.hostelchatbot.DTO.Student;
import com.hostelchatbot.hostelchatbot.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student addStudent(Student student)
    {
        if (studentRepository.existsByRollNo(student.getRollNo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Roll number is already registered");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        studentRepository.save(student);
        return student;
    }

    public boolean passwordMatches(String rollNo, String password) {
        return studentRepository.findByRollNo(rollNo)
                .map(student -> passwordEncoder.matches(password, student.getPassword()))
                .orElse(false);
    }

    public Student authenticate(String rollNo, String password) {
        return studentRepository.findByRollNo(rollNo)
                .filter(student -> passwordEncoder.matches(password, student.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid roll number or password"));
    }

}
