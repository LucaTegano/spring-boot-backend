package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

// A DTO for identifying a collaborator by their username
@Getter
@Setter
public class NoteCollaboratorDto {
    private String username;
}