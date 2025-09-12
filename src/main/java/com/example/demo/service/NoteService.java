package com.example.demo.service;

import com.example.demo.dto.NoteListItemDto;
import com.example.demo.dto.RecentNotesDto;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.NoteRepository;

//import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    /**
     * Search notes by title or content for a specific user
     * 
     * @param query    The search query
     * @param username The username of the user
     * @return A list of NoteListItemDto matching the search query
     */
    @Transactional(readOnly = true)
    public List<NoteListItemDto> searchNotesForUser(String query, String username) {
        User user = userService.getUserByUsername(username);
        // Get all notes for the user
        List<Note> notes = noteRepository.findByOwner_IdOrderByLastActivityDesc(user.getId());

        // Handle null or empty query
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return notes.stream()
                .filter(note -> (note.getTitle().toLowerCase().contains(query.toLowerCase())))
                .limit(5) // Limit to 5 results for performance
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

    /**
     * Get recent notes categorized by time periods
     * Returns only the first 3 most recent notes for each category
     * Notes from the previous 7 days are prioritized (won't appear in previous 30
     * days)
     * If there are no notes in previous 7 days and previous 30 days, returns latest
     * 5 notes
     */
    @Transactional(readOnly = true)
    public RecentNotesDto getRecentNotesForUser(String username) {
        User user = userService.getUserByUsername(username);
        // Get notes sorted by lastActivity in descending order (most recent first)
        List<Note> notes = noteRepository.findByOwner_IdOrderByLastActivityDesc(user.getId());

        // Current date for comparison
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        // Filter notes by date ranges
        List<NoteListItemDto> previous7DaysNotes = notes.stream()
                .filter(note -> note.getLastActivity().isAfter(sevenDaysAgo))
                .limit(3)
                .map(this::mapToNoteListItemDto)
                .collect(Collectors.toList());

        // For previous 30 days, we only want notes that are older than 7 days but
        // within 30 days
        List<NoteListItemDto> previous30DaysNotes = notes.stream()
                .filter(note -> note.getLastActivity().isAfter(thirtyDaysAgo) &&
                        note.getLastActivity().isBefore(sevenDaysAgo))
                .limit(3)
                .map(this::mapToNoteListItemDto)
                .collect(Collectors.toList());

        // Show the latest notes in general if they are coverd by previous categories
        // they are not included here but otherwise they are included
        List<NoteListItemDto> latestNotes = notes.stream()
                .filter(note -> note.getLastActivity().isBefore(thirtyDaysAgo))
                .limit(5)
                .map(this::mapToNoteListItemDto)
                .collect(Collectors.toList());

        RecentNotesDto result = new RecentNotesDto();
        result.setPrevious7Days(previous7DaysNotes);
        result.setPrevious30Days(previous30DaysNotes);
        result.setLatestNotes(latestNotes);

        return result;
    }

    private NoteListItemDto mapToNoteListItemDto(Note note) {
        NoteListItemDto dto = new NoteListItemDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setLastActivity(note.getLastActivity());
        dto.setFormattedDate(formatDate(note.getLastActivity()));
        return dto;
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

    public Note getNoteAndVerifyOwner(Long noteId, String username) {
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
     * 
     * public Note addCollaborator(Long noteId, String collaboratorUsername, String
     * ownerUsername) {
     * // Call the new helper method to find the note and verify ownership
     * Note note = getNoteAndVerifyOwner(noteId, ownerUsername);
     * 
     * User collaborator = userService.getUserByUsername(collaboratorUsername);
     * 
     * if (note.getOwner().equals(collaborator)) {
     * throw new IllegalArgumentException("Cannot add the owner as a
     * collaborator.");
     * }
     * 
     * note.addCollaborator(collaborator);
     * return noteRepository.save(note);
     * }
     * 
     * 
     * Removes a collaborator from a note. Only the owner can remove collaborators.
     * 
     * 
     * public Note removeCollaborator(Long noteId, String collaboratorUsername,
     * String ownerUsername) {
     * // Call the new helper method to find the note and verify ownership
     * Note note = getNoteAndVerifyOwner(noteId, ownerUsername);
     * 
     * User collaborator = userService.getUserByUsername(collaboratorUsername);
     * 
     * note.removeCollaborator(collaborator);
     * return noteRepository.save(note);
     * }
     */
}