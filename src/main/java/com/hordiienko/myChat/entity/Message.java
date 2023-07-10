package com.hordiienko.myChat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
@Builder
public class Message {
    @Id
    private String id;
    @JsonIgnore
    private String userId;
    private String userName;
    private LocalDateTime creationDateTime;
    private String title;
    private String text;
    private boolean edited;
    private LocalDateTime editDateTime;
}
