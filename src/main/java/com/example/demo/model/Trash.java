package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a trashed note.
 * When a user deletes a note, it's moved to the trash instead of being
 * permanently deleted.
 */
@Entity
@Table(name = "trash")
@Getter
@Setter
@NoArgsConstructor
public class Trash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store the original note data
    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "deleted_at", nullable = false, updatable = false)
    private LocalDateTime deletedAt;

    // Reference to the original note ID
    @Column(name = "original_note_id")
    private Long originalNoteId;

    // Reference to the owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Store collaborator information as JSON or comma-separated values
    @Column(name = "collaborators_info", columnDefinition = "TEXT")
    private String collaboratorsInfo;

    /**
     * Constructor to create a Trash entity from a Note
     * 
     * @param note The note to be moved to trash
     */
    public Trash(Note note) {
        this.title = note.getTitle();
        this.content = note.getContent();
        this.createdAt = note.getCreatedAt();
        this.originalNoteId = note.getId();

        // Store collaborator information
        if (note.getCollaborators() != null && !note.getCollaborators().isEmpty()) {
            StringBuilder collaboratorsBuilder = new StringBuilder();
            for (User collaborator : note.getCollaborators()) {
                if (collaboratorsBuilder.length() > 0) {
                    collaboratorsBuilder.append(",");
                }
                collaboratorsBuilder.append(collaborator.getId());
            }
            this.collaboratorsInfo = collaboratorsBuilder.toString();
        }

        this.owner = note.getOwner();
    }
}