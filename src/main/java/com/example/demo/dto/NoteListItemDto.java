package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoteListItemDto {
    private Long id;
    private String title;
    private LocalDateTime lastActivity;
    private String formattedDate;
}