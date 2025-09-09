package com.example.demo.service;

import com.example.demo.dto.NoteListItemDto;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<NoteListItemDto> getNoteListItemsForUser(String username) {
        User user = userService.getUserByUsername(username);
        // Get notes sorted by lastActivity in descending order (most recent first)
        List<Note> notes = noteRepository.findByOwner_IdOrderByLastActivityDesc(user.getId());
        
        return notes.stream()
                .map(note -> {
                    NoteListItemDto dto = new NoteListItemDto();
                    dto.setId(note.getId());
                    dto.setTitle(note.getTitle());
                    dto.setLastActivity(note.getLastActivity());
                    dto.setFormattedDate(formatDate(note.getLastActivity()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(dateTime, now);
        
        if (daysBetween < 1) {
            long hoursBetween = ChronoUnit.HOURS.between(dateTime, now);
            if (hoursBetween < 1) {
                long minutesBetween = ChronoUnit.MINUTES.between(dateTime, now);
                if (minutesBetween < 1) {
                    return "Now";
                }
                return minutesBetween + " minute" + (minutesBetween != 1 ? "s" : "") + " ago";
            }
            return hoursBetween + " hour" + (hoursBetween != 1 ? "s" : "") + " ago";
        } else if (daysBetween < 7) {
            return daysBetween + " day" + (daysBetween != 1 ? "s" : "") + " ago";
        } else {
            return dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() + "/" + dateTime.getYear();
        }
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