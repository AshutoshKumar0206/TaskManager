package com.taskManager.tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Error Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class ErrorResponse {
    private String error;
    private String message;
    private Integer status;
}

