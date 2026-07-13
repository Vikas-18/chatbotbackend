package com.hostelchatbot.hostelchatbot.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Complaint {
    @Id
    String id;

    String name;

    String roomNo;

    String description;

    String type;

    String hostelName;
    
    boolean resolved;

}
