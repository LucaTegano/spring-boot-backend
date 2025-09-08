package com.example.demo.service;

import com.example.demo.dto.NoteDto;
import com.example.demo.exception.NoteNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Helper method to fetch a user by username or throw a standard exception.
     */
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Efficiently retrieves all notes a user can access (as owner or collaborator).
     *
     * @param username The username of the currently authenticated user.
     * @return A list of notes.
     */
    @Transactional(readOnly = true)
    public List<Note> getAllNotesForUser(String username) {
        User user = getUserByUsername(username);
        // This single query is highly efficient for fetching all accessible notes.
        return noteRepository.findAllNotesForUser(user);
    }

    /**
     * Retrieves a single note by its ID, ensuring the user has access.
     *
     * @param noteId The ID of the note.
     * @param username The username of the user requesting the note.
     * @return The found Note.
     * @throws NoteNotFoundException if no note with the ID is found.
     * @throws UnauthorizedActionException if the user is not the owner or a collaborator.
     */
    @Transactional(readOnly = true)
    public Note getNoteByIdForUser(Long noteId, String username) {
        User user = getUserByUsername(username);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));

        // Security Check: Is the user the owner or in the set of collaborators?
        if (!note.getOwner().equals(user) && !note.getCollaborators().contains(user)) {
            throw new UnauthorizedActionException("User does not have access to this note.");
        }
        return note;
    }


    /**
     * Creates a new note and assigns the current user as the owner.
     *
     * @param noteRequest The DTO containing the note's title and content.
     * @param username The username of the note's owner.
     * @return The newly created and persisted Note.
     */
    public Note createNote(NoteDto noteRequest, String username) {
        User owner = getUserByUsername(username);
        Note newNote = new Note();
        newNote.setTitle(noteRequest.getTitle());
        newNote.setContent(noteRequest.getContent());
        newNote.setOwner(owner);
        return noteRepository.save(newNote);
    }

    /**
     * Updates an existing note's title and content.
     *
     * @param noteId The ID of the note to update.
     * @param noteDetails The DTO with the new details.
     * @param username The user attempting the update.
     * @return The updated note.
     * @throws UnauthorizedActionException if the user is not the owner or a collaborator.
     */
    @Transactional
    public Note updateNote(Long noteId, NoteDto noteDetails, String username) {
        // We can reuse getNoteByIdForUser as it includes the necessary security check.
        Note note = getNoteByIdForUser(noteId, username);

        note.setTitle(noteDetails.getTitle());
        note.setContent(noteDetails.getContent());
        // The @UpdateTimestamp will automatically be handled by Hibernate upon saving.
        return noteRepository.save(note);
    }

    /**
     * Deletes a note. This action is restricted to the note's owner.
     *
     * @param noteId The ID of the note to delete.
     * @param username The user attempting the deletion.
     * @throws UnauthorizedActionException if the user is not the owner.
     */
    public void deleteNote(Long noteId, String username) {
        User user = getUserByUsername(username);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));

        // Stricter Security Check: Only the owner can delete.
        if (!note.getOwner().equals(user)) {
            throw new UnauthorizedActionException("Only the owner can delete this note.");
        }

        noteRepository.delete(note);
    }

    /**
     * Adds a collaborator to a note. Only the owner can add collaborators.
     *
     * @param noteId The ID of the note.
     * @param collaboratorUsername The username of the user to add as a collaborator.
     * @param ownerUsername The user attempting to add the collaborator.
     * @return The updated note.
     */
    @Transactional
    public Note addCollaborator(Long noteId, String collaboratorUsername, String ownerUsername) {
        User owner = getUserByUsername(ownerUsername);
        User collaborator = getUserByUsername(collaboratorUsername);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));

        if (!note.getOwner().equals(owner)) {
            throw new UnauthorizedActionException("Only the owner can add collaborators.");
        }
        if (owner.equals(collaborator)) {
             throw new IllegalArgumentException("Cannot add the owner as a collaborator.");
        }

        note.addCollaborator(collaborator);
        return noteRepository.save(note);
    }

    /**
     * Removes a collaborator from a note. Only the owner can remove collaborators.
     *
     * @param noteId The ID of the note.
     * @param collaboratorUsername The username of the collaborator to remove.
     * @param ownerUsername The user attempting to remove the collaborator.
     * @return The updated note.
     */
    @Transactional
    public Note removeCollaborator(Long noteId, String collaboratorUsername, String ownerUsername) {
        User owner = getUserByUsername(ownerUsername);
        User collaborator = getUserByUsername(collaboratorUsername);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));

        if (!note.getOwner().equals(owner)) {
            throw new UnauthorizedActionException("Only the owner can remove collaborators.");
        }

        note.removeCollaborator(collaborator);
        return noteRepository.save(note);
    }
}