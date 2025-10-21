package com.taskManager.tasks.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    
    private String title;
    
    private String description;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}