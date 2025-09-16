package com.example.demo.controller;

import com.example.demo.dto.AiChatMessageDto;
import com.example.demo.dto.SendMessageRequestDto;
import com.example.demo.service.ai.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/note/{noteId}/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    /**
     * Sends a new message to the chat associated with a specific note
     * and gets a response from the AI.
     */
    @PostMapping
    public ResponseEntity<AiChatMessageDto> sendMessage(
            @PathVariable Long noteId,
            @Valid @RequestBody SendMessageRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        AiChatMessageDto aiResponse = aiChatService.sendMessage(noteId, request.getMessage(), username);
        return ResponseEntity.ok(aiResponse);
    }

    /**
     * Retrieves the entire chat history for a specific note.
     */
    @GetMapping
    public ResponseEntity<List<AiChatMessageDto>> getChatHistory(
            @PathVariable Long noteId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        List<AiChatMessageDto> history = aiChatService.getChatHistory(noteId, username);
        return ResponseEntity.ok(history);
    }
}