package com.hostelchatbot.hostelchatbot.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document("Students")
@Getter
@Setter
public class Student {
    @Id
    String id;

    String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Indexed(unique = true)
    String rollNo;

    String hostelName;

}
