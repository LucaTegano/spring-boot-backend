package com.example.demo.service;

import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public List<Note> getAllNotesForUser(String username) {
        User user = userService.getUserByUsername(username);
        return noteRepository.findByOwner_Id(user.getId());
    }

    public Note createNote(Note noteRequest, String username) {
        User owner = userService.getUserByUsername(username);
        noteRequest.setOwner(owner);
        return noteRepository.save(noteRequest);
    }

    @Transactional
    public Note updateNote(Long noteId, Note noteRequest, String username) {
        Note note = getNoteAndVerifyOwner(noteId, username);
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        return noteRepository.save(note);
    }

    public void deleteNote(Long noteId, String username) {
        Note noteToDelete = getNoteAndVerifyOwner(noteId, username);
        noteRepository.delete(noteToDelete);
    }

    @Transactional(readOnly = true)
    public Note getNoteByIdForUser(Long noteId, String username) {
        Note note = getNoteAndVerifyOwner(noteId, username);
        return note;
    }

    private Note getNoteAndVerifyOwner(Long noteId, String username) {
        User owner = userService.getUserByUsername(username);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));
        if (!note.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("User not authorized for this task");
        }

        return note;
    }

    /**
     * Adds a collaborator to a note. Only the owner can add collaborators.
     
    public Note addCollaborator(Long noteId, String collaboratorUsername, String ownerUsername) {
        // Call the new helper method to find the note and verify ownership
        Note note = getNoteAndVerifyOwner(noteId, ownerUsername);
        
        User collaborator = userService.getUserByUsername(collaboratorUsername);
        
        if (note.getOwner().equals(collaborator)) {
             throw new IllegalArgumentException("Cannot add the owner as a collaborator.");
        }

        note.addCollaborator(collaborator);
        return noteRepository.save(note);
    }

    
     Removes a collaborator from a note. Only the owner can remove collaborators.
     
    
    public Note removeCollaborator(Long noteId, String collaboratorUsername, String ownerUsername) {
        // Call the new helper method to find the note and verify ownership
        Note note = getNoteAndVerifyOwner(noteId, ownerUsername);

        User collaborator = userService.getUserByUsername(collaboratorUsername);

        note.removeCollaborator(collaborator);
        return noteRepository.save(note);
    }
    */
}