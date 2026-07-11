package com.hostelchatbot.hostelchatbot.controllers;

import com.hostelchatbot.hostelchatbot.DTO.LoginRequest;
import com.hostelchatbot.hostelchatbot.DTO.LoginResponse;
import com.hostelchatbot.hostelchatbot.DTO.Student;
import com.hostelchatbot.hostelchatbot.services.StudentService;
import com.hostelchatbot.hostelchatbot.services.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
    private final StudentService studentService;
    private final JwtService jwtService;

    public StudentController(StudentService studentService, JwtService jwtService) {
        this.studentService = studentService;
        this.jwtService = jwtService;
    }
    @PostMapping("/addStudent")
    public Student addStudent(@RequestBody Student student)
    {
        return studentService.addStudent(student);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Student student = studentService.authenticate(loginRequest.rollNo(), loginRequest.password());
        return jwtService.createToken(student);
    }

}
