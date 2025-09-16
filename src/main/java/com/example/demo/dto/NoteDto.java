package com.example.demo.dto;

import com.example.demo.model.Note;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isCollaborative;
    private int participantCount;

    public NoteDto(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.createdAt = note.getCreatedAt();
        this.updatedAt = note.getLastActivity();
        this.isCollaborative = note.getCollaborators() != null && !note.getCollaborators().isEmpty();
        this.participantCount = note.getParticipantCount();
    }
}
