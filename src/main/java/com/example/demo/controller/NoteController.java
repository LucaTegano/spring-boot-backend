package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<Note> getAllMyNotes(@AuthenticationPrincipal UserDetails userDetails) {
        return noteService.getAllNotesForUser(userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Note note = noteService.getNoteByIdForUser(id, userDetails.getUsername());
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public Note createNote(@RequestBody Note note, @AuthenticationPrincipal UserDetails userDetails) {
        return noteService.createNote(note, userDetails.getUsername());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateNote(@PathVariable Long id, @RequestBody Note note, @AuthenticationPrincipal UserDetails userDetails) {
        noteService.updateNote(id, note, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        noteService.deleteNote(id, userDetails.getUsername());
        return ResponseEntity.ok().build(); // Standard practice to return 200 OK with no body on successful DELETE
    }

    // --- Collaborator Management Endpoints ---

    /**
     * Adds a collaborator to a note. Only the owner can add collaborators.
     
    @PostMapping("/{id}/collaborators")
    public ResponseEntity<Note> addCollaborator(
            @PathVariable Long id,
            @RequestBody NoteCollaboratorDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Note updatedNote = noteService.addCollaborator(id, request.getUsername(), userDetails.getUsername());
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}/collaborators")
    public ResponseEntity<Note> removeCollaborator(
            @PathVariable Long id,
            @RequestBody NoteCollaboratorDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Note updatedNote = noteService.removeCollaborator(id, request.getUsername(), userDetails.getUsername());
        return ResponseEntity.ok(updatedNote);
    }
    */
}