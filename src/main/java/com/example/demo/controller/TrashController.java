package com.example.demo.controller;

import com.example.demo.dto.TrashDto;
import com.example.demo.service.TrashService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trash")
public class TrashController {

    private final TrashService trashService;

    public TrashController(TrashService trashService) {
        this.trashService = trashService;
    }

    /**
     * Move a note to trash
     */
    @PostMapping("/move/{id}")
    public ResponseEntity<?> moveToTrash(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        trashService.moveToTrash(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Get all trashed items for the user
     */
    @GetMapping
    public List<TrashDto> getTrashedItems(@AuthenticationPrincipal UserDetails userDetails) {
        return trashService.getTrashedItems(userDetails.getUsername());
    }

    /**
     * Restore a note from trash
     */
    @PostMapping("/restore/{id}")
    public ResponseEntity<?> restoreFromTrash(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        trashService.restoreFromTrash(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Permanently delete a note from trash
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> permanentlyDelete(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        trashService.permanentlyDelete(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    /**
     * Empty the trash
     */
    @DeleteMapping
    public ResponseEntity<?> emptyTrash(@AuthenticationPrincipal UserDetails userDetails) {
        trashService.emptyTrash(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}