package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

// A simple DTO for handling note creation and update requests
@Getter
@Setter
public class NoteDto {
    private String title;
    private String content;
}