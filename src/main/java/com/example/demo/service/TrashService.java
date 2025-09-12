package com.example.demo.service;

import com.example.demo.dto.TrashDto;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.model.Trash;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.TrashRepository;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrashService {

    private final TrashRepository trashRepository;
    private final NoteRepository noteRepository;
    private final UserService userService;

    public TrashService(TrashRepository trashRepository, NoteRepository noteRepository, UserService userService) {
        this.trashRepository = trashRepository;
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    /**
     * Move a note to trash
     * 
     * @param noteId   The ID of the note to move to trash
     * @param username The username of the user
     */
    @Transactional
    public void moveToTrash(Long noteId, String username) {
        User user = userService.getUserByUsername(username);
        Note note = noteRepository.findByIdAndOwner(noteId, user)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));

        // Create a trash entity from the note
        Trash trash = new Trash(note);
        trash.setDeletedAt(java.time.LocalDateTime.now());

        // Save the trash entity
        trashRepository.save(trash);

        // Delete the original note
        noteRepository.delete(note);
    }

    /**
     * Get all trashed items for a user
     * 
     * @param username The username of the user
     * @return A list of trashed items
     */
    @Transactional(readOnly = true)
    public List<TrashDto> getTrashedItems(String username) {
        User user = userService.getUserByUsername(username);
        List<Trash> trashedItems = trashRepository.findByOwnerOrderByDeletedAtDesc(user);

        return trashedItems.stream()
                .map(this::mapToTrashDto)
                .collect(Collectors.toList());
    }

    /**
     * Restore a note from trash
     * 
     * @param trashId  The ID of the trashed item
     * @param username The username of the user
     */
    @Transactional
    public void restoreFromTrash(Long trashId, String username) {
        User user = userService.getUserByUsername(username);
        Trash trash = trashRepository.findByIdAndOwner(trashId, user);

        if (trash == null) {
            throw new RuntimeException("Trashed item not found with id: " + trashId);
        }

        // Create a new note from the trash data
        Note note = new Note();
        note.setTitle(trash.getTitle());
        note.setContent(trash.getContent());
        note.setCreatedAt(trash.getCreatedAt());
        note.setLastActivity(java.time.LocalDateTime.now());
        note.setOwner(trash.getOwner());

        // Save the restored note
        noteRepository.save(note);

        // Delete the trash entity
        trashRepository.delete(trash);
    }

    /**
     * Permanently delete a note from trash
     * 
     * @param trashId  The ID of the trashed item
     * @param username The username of the user
     */
    @Transactional
    public void permanentlyDelete(Long trashId, String username) {
        User user = userService.getUserByUsername(username);
        Trash trash = trashRepository.findByIdAndOwner(trashId, user);

        if (trash == null) {
            throw new RuntimeException("Trashed item not found with id: " + trashId);
        }

        // Delete the trash entity
        trashRepository.delete(trash);
    }

    /**
     * Empty the trash by permanently deleting all trashed items
     * 
     * @param username The username of the user
     */
    @Transactional
    public void emptyTrash(String username) {
        User user = userService.getUserByUsername(username);
        List<Trash> trashedItems = trashRepository.findByOwnerOrderByDeletedAtDesc(user);

        // Delete all trashed items
        trashRepository.deleteAll(trashedItems);
    }

    /**
     * Map a Trash entity to a TrashDto
     * 
     * @param trash The trash entity
     * @return The trash DTO
     */
    private TrashDto mapToTrashDto(Trash trash) {
        TrashDto dto = new TrashDto();
        dto.setId(trash.getId());
        dto.setTitle(trash.getTitle());
        dto.setContent(trash.getContent());
        dto.setCreatedAt(trash.getCreatedAt());
        dto.setDeletedAt(trash.getDeletedAt());
        dto.setOriginalNoteId(trash.getOriginalNoteId());

        // Calculate participant count from stored collaborator info
        int participantCount = 1; // Owner
        if (trash.getCollaboratorsInfo() != null && !trash.getCollaboratorsInfo().isEmpty()) {
            String[] collaboratorIds = trash.getCollaboratorsInfo().split(",");
            participantCount += collaboratorIds.length;
        }
        dto.setParticipantCount(participantCount);

        return dto;
    }
}