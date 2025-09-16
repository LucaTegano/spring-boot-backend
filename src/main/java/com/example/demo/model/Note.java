package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a collaborative Note.
 * Each note has a single owner and can be shared with multiple collaborators.
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob // Specifies that this should be mapped to a Large Object type in the DB
    @Column(columnDefinition = "TEXT") // More explicit for cross-database compatibility
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;

    // --- Relationships ---

    /**
     * The user who owns this note.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    /**
     * The group this note belongs to (optional).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    /**
     * The set of users who can collaborate on this note.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "note_collaborators", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> collaborators = new HashSet<>();

    // --- Derived (Calculated) Data ---

    /**
     * Calculates the total number of participants (owner + collaborators).
     * This is not a database column. @Transient is optional here because
     * the absence of a @Column annotation and the presence of a getter
     * without a field is enough for JPA.
     *
     * @return The total count of participants.
     */
    @Transient
    public int getParticipantCount() {
        // The owner (1) + the number of collaborators
        return 1 + (collaborators != null ? collaborators.size() : 0);
    }

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @OrderBy("createdAt ASC")
    private List<AiChatMessage> aiChatHistory = new ArrayList<>();

    // --- Helper Methods ---

    public void addCollaborator(User user) {
        this.collaborators.add(user);
    }

    public void removeCollaborator(User user) {
        this.collaborators.remove(user);
    }
}