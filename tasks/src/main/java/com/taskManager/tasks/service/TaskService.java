package com.taskManager.tasks.service;

import com.taskManager.tasks.dto.TaskRequest;
import com.taskManager.tasks.model.AuditLog;
import com.taskManager.tasks.model.Task;
import com.taskManager.tasks.repository.AuditLogRepository;
import com.taskManager.tasks.repository.TaskRepository;
import com.taskManager.tasks.util.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final AuditLogRepository auditLogRepository;
    
    public Page<Task> getAllTasks(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (search != null && !search.trim().isEmpty()) {
            return taskRepository.searchByTitleOrDescription(search.trim(), pageable);
        }
        
        return taskRepository.findAll(pageable);
    }
    
    @Transactional
    public Task createTask(TaskRequest request) {
        // Sanitize inputs
        String sanitizedTitle = SanitizerUtil.sanitize(request.getTitle());
        String sanitizedDescription = SanitizerUtil.sanitize(request.getDescription());
        
        Task task = new Task(sanitizedTitle, sanitizedDescription);
        Task savedTask = taskRepository.save(task);
        
        // Create audit log
        Map<String, Object> content = new HashMap<>();
        content.put("title", savedTask.getTitle());
        content.put("description", savedTask.getDescription());
        
        AuditLog auditLog = new AuditLog("Create Task", savedTask.getId(), content);
        auditLogRepository.save(auditLog);
        
        log.info("Task created with ID: {}", savedTask.getId());
        return savedTask;
    }
    
    @Transactional
    public Task updateTask(String id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Track changes
        Map<String, Object> changes = new HashMap<>();
        
        String sanitizedTitle = SanitizerUtil.sanitize(request.getTitle());
        String sanitizedDescription = SanitizerUtil.sanitize(request.getDescription());
        
        if (!task.getTitle().equals(sanitizedTitle)) {
            changes.put("title", sanitizedTitle);
            task.setTitle(sanitizedTitle);
        }
        
        if (!task.getDescription().equals(sanitizedDescription)) {
            changes.put("description", sanitizedDescription);
            task.setDescription(sanitizedDescription);
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Create audit log only if there were changes
        if (!changes.isEmpty()) {
            AuditLog auditLog = new AuditLog("Update Task", updatedTask.getId(), changes);
            auditLogRepository.save(auditLog);
        }
        
        log.info("Task updated with ID: {}", updatedTask.getId());
        return updatedTask;
    }
    
    @Transactional
    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        taskRepository.deleteById(id);
        
        // Create audit log
        AuditLog auditLog = new AuditLog("Delete Task", id, null);
        auditLogRepository.save(auditLog);
        
        log.info("Task deleted with ID: {}", id);
    }
    
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}
