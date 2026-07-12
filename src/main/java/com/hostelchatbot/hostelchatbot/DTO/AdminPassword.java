package com.hostelchatbot.hostelchatbot.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("adminPasswords")
@Getter
@Setter
public class AdminPassword {
    @Id
    private String id;

    private String passwordHash;

    private Instant updatedAt;
}
