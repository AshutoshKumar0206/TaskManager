package com.taskManager.tasks.controller;

import com.taskManager.tasks.dto.TaskRequest;
import com.taskManager.tasks.model.AuditLog;
import com.taskManager.tasks.model.Task;
import com.taskManager.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping("/tasks")
    public ResponseEntity<Map<String, Object>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search) {
        
        try {
            Page<Task> taskPage = taskService.getAllTasks(page, size, search);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tasks", taskPage.getContent());
            response.put("currentPage", taskPage.getNumber());
            response.put("totalItems", taskPage.getTotalElements());
            response.put("totalPages", taskPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching tasks", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch tasks");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request) {
        try {
            Task task = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception e) {
            log.error("Error creating task", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request) {
        try {
            Task task = taskService.updateTask(id, request);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            log.error("Error updating task", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error updating task", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update task");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        try {
            taskService.deleteTask(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deleting task", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error deleting task", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete task");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/logs")
    public ResponseEntity<?> getAuditLogs() {
        try {
            List<AuditLog> logs = taskService.getAllAuditLogs();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching audit logs", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch audit logs");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
