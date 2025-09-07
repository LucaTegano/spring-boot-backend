package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a single task that belongs to a Group.
 * It can be assigned to a specific user within that group.
 */
@Entity
@Table(name = "group_tasks")
@Getter
@Setter
@NoArgsConstructor
public class GroupTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean completed = false; // Default to not completed

    // --- Relationships ---

    /**
     * The group this task belongs to. This is the owning side of the relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    /**
     * The user to whom this task is assigned. Can be null if unassigned.
     * NOTE: Application logic should ensure the assignee is a member of the group.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id") // This column can be null
    private User assignee;

}