package com.example.demo.repository;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// It's a best practice to use JpaRepository, which extends CrudRepository and
// adds more features.
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Finds all notes owned by a specific user.
     * Replaces the old findByUser_Id. The property is now 'owner'.
     * 
     * @param owner The user who owns the notes.
     * @return A list of notes owned by the user.
     */
    List<Note> findByOwner(User owner);

    // You can also query by the owner's ID if you prefer:
    // List<Note> findByOwner_Id(Long ownerId);

    /**
     * Finds all notes where the given user is a collaborator.
     * 'Contains' is the correct keyword for checking membership in a collection.
     * Replaces the old findByCollaborators_Id.
     * 
     * @param user The user to check for as a collaborator.
     * @return A list of notes where the user is a collaborator.
     */
    List<Note> findByCollaboratorsContains(User user);

    /**
     * Finds a specific note by its ID, but only if it is owned by the provided
     * user.
     * This is an important security check to prevent users from accessing notes
     * they don't own.
     * Replaces the old findByIdAndUser.
     * 
     * @param id    The ID of the note.
     * @param owner The user who must own the note.
     * @return An Optional containing the note if found and owned by the user.
     */
    Optional<Note> findByIdAndOwner(Long id, User owner);

    /**
     * Finds all tasks owned by a specific user, identified by their ID.
     * The return type is List<PersonalTask>. If the user has no tasks,
     * this method will correctly return an empty list.
     *
     * @param userId The ID of the user to find tasks for.
     * @return A list of tasks for the given user; never null.
     */
    List<Note> findByOwner_Id(Long userId);

    /**
     * Finds all notes owned by a specific user, identified by their ID,
     * sorted by lastActivity in descending order (most recent first).
     *
     * @param userId The ID of the user to find notes for.
     * @return A list of notes for the given user, sorted by lastActivity
     *         descending; never null.
     */
    List<Note> findByOwner_IdOrderByLastActivityDesc(Long userId);

    /**
     * Finds all notes associated with a specific group.
     *
     * @param groupId The ID of the group to find notes for.
     * @return A list of notes associated with the given group.
     */
    List<Note> findByGroup_Id(Long groupId);
}