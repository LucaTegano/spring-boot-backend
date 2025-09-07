package com.example.demo.repository;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// It's a best practice to use JpaRepository, which extends CrudRepository and adds more features.
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Finds all notes owned by a specific user.
     * Replaces the old findByUser_Id. The property is now 'owner'.
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
     * @param user The user to check for as a collaborator.
     * @return A list of notes where the user is a collaborator.
     */
    List<Note> findByCollaboratorsContains(User user);

    /**
     * Finds a specific note by its ID, but only if it is owned by the provided user.
     * This is an important security check to prevent users from accessing notes they don't own.
     * Replaces the old findByIdAndUser.
     * @param id The ID of the note.
     * @param owner The user who must own the note.
     * @return An Optional containing the note if found and owned by the user.
     */
    Optional<Note> findByIdAndOwner(Long id, User owner);
    
    /**
     * A very useful query: finds all notes accessible to a user,
     * either because they are the owner OR they are a collaborator.
     * This often saves you from making two separate database calls.
     * @param user The user to find notes for.
     * @return A list of all notes accessible by the user.
     */
    @Query("SELECT n FROM Note n WHERE n.owner = :user OR :user MEMBER OF n.collaborators")
    List<Note> findAllNotesForUser(@Param("user") User user);
}