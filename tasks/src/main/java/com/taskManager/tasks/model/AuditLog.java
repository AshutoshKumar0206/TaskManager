package com.taskManager.tasks.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;
    
    private LocalDateTime timestamp;
    
    private String action;
    
    private String taskId;
    
    private Map<String, Object> updatedContent;
    
    private String notes;
    
    public AuditLog(String action, String taskId, Map<String, Object> updatedContent) {
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.taskId = taskId;
        this.updatedContent = updatedContent;
        this.notes = null;
    }
}