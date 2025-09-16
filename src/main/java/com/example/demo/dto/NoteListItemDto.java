package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteListItemDto {
    private Long id;
    private String title;
    private LocalDateTime lastActivity;
    private String formattedDate;
}