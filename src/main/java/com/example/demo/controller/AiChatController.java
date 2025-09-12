// src/main/java/com/example/demo/controller/AiChatController.java
package com.example.demo.controller;

import com.example.demo.dto.AiChatMessageDto;
import com.example.demo.dto.AiChatRequestDto;
import com.example.demo.service.ai.AiChatService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/note/{noteId}/chat")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * Gets the entire chat history for a specific note.
     */
    @GetMapping
    public ResponseEntity<List<AiChatMessageDto>> getChatHistory(@PathVariable Long noteId) {
        List<AiChatMessageDto> history = aiChatService.getChatHistory(noteId);
        return ResponseEntity.ok(history);
    }

    /**
     * Sends a new message and streams the AI response.
     * Produces a 'text/event-stream' which is ideal for clients to consume.
     */
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> streamChat(
            @PathVariable Long noteId,
            @RequestBody AiChatRequestDto request) {

        StreamingResponseBody stream = aiChatService.streamChatResponse(noteId, request.getMessage());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(stream);
    }
}