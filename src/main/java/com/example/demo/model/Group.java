package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a Group, which can contain multiple users (members) and tasks.
 * Each group has a single owner.
 */
@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // --- Relationships ---

    /**
     * The user who created/owns the group.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * The set of users who are members of this group.
     * This is a Many-to-Many relationship, managed by a join table named
     * "group_members".
     */
    @ManyToMany
    @JoinTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    /**
     * The list of tasks associated with this group.
     * If a group is deleted, all its tasks are also deleted (CascadeType.ALL).
     */
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTask> tasks;

    /**
     * The list of notes associated with this group.
     * If a group is deleted, all its notes are also deleted (CascadeType.ALL).
     */
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;

    // --- Helper Methods for managing relationships ---

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }

    public void addTask(GroupTask task) {
        this.tasks.add(task);
        task.setGroup(this);
    }

    public void removeTask(GroupTask task) {
        this.tasks.remove(task);
        task.setGroup(null);
    }

    public void addNote(Note note) {
        this.notes.add(note);
        note.setGroup(this);
    }

    public void removeNote(Note note) {
        this.notes.remove(note);
        note.setGroup(null);
    }
}