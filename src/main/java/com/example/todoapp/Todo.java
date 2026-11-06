package com.example.todoapp;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Todo {

    private Long id;
    private String title;
    private String detail;
    private String category;
    private Integer priority;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
