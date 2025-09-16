package com.example.demo.service.ai;

import com.example.demo.dto.AiChatMessageDto;
import com.example.demo.model.AiChatMessage;
import com.example.demo.model.AiMessageRole;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.AiChatMessageRepository;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private final NoteRepository noteRepository;
    private final AiChatMessageRepository aiChatMessageRepository;
    private final GeminiService geminiService;
    private final UserService userService; // Assuming you have a UserService

    @Transactional
    public AiChatMessageDto sendMessage(Long noteId, String userMessageContent, String username) {
        User currentUser = userService.getUserByUsername(username);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));

        // Security Check: Ensure the user can access this note
        checkNoteAccess(note, currentUser);

        // 1. Save the user's message
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setNote(note);
        userMessage.setRole(AiMessageRole.USER);
        userMessage.setContent(userMessageContent);
        aiChatMessageRepository.save(userMessage);

        // 2. Build the complete prompt for the AI
        String fullPrompt = buildPrompt(note, userMessageContent);

        // 3. Call the AI service
        String aiResponseContent = geminiService.askGemini(fullPrompt);

        // 4. Save the AI's response
        AiChatMessage aiMessage = new AiChatMessage();
        aiMessage.setNote(note);
        aiMessage.setRole(AiMessageRole.MODEL);
        aiMessage.setContent(aiResponseContent);
        AiChatMessage savedAiMessage = aiChatMessageRepository.save(aiMessage);

        // 5. Update the note's last activity timestamp
        note.setLastActivity(LocalDateTime.now());
        noteRepository.save(note);

        // 6. Return the new AI message as a DTO
        return new AiChatMessageDto(
                savedAiMessage.getId(),
                savedAiMessage.getRole(),
                savedAiMessage.getContent(),
                savedAiMessage.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<AiChatMessageDto> getChatHistory(Long noteId, String username) {
        User currentUser = userService.getUserByUsername(username);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));

        // Security Check
        checkNoteAccess(note, currentUser);

        List<AiChatMessage> messages = aiChatMessageRepository.findByNoteIdOrderByCreatedAtAsc(noteId);

        return messages.stream()
                .map(msg -> new AiChatMessageDto(msg.getId(), msg.getRole(), msg.getContent(), msg.getCreatedAt()))
                .collect(Collectors.toList());
    }

    private String buildPrompt(Note note, String newQuery) {
        StringBuilder promptBuilder = new StringBuilder();

        // System-level instruction
        promptBuilder.append("You are a helpful assistant integrated into a note-taking app. ");
        promptBuilder.append("The user is currently working on the note provided below. ");
        promptBuilder.append("Your primary context is this note. All your responses should be relevant to it. ");
        promptBuilder.append(
                "Do not refer to the note as 'the provided note', just use its content as your knowledge base.\n\n");

        // The Note Content (The most important context!)
        promptBuilder.append("--- NOTE START ---\n");
        promptBuilder.append("Title: ").append(note.getTitle()).append("\n\n");
        promptBuilder.append(note.getContent()).append("\n");
        promptBuilder.append("--- NOTE END ---\n\n");

        // The Chat History
        promptBuilder.append("--- CHAT HISTORY START ---\n");
        List<AiChatMessage> history = aiChatMessageRepository.findByNoteIdOrderByCreatedAtAsc(note.getId());
        for (AiChatMessage message : history) {
            promptBuilder.append(message.getRole().name()).append(": ").append(message.getContent()).append("\n");
        }
        // The history already includes the latest user message we just saved, so we
        // don't need to append `newQuery` again.
        promptBuilder.append("--- CHAT HISTORY END ---\n\n");

        promptBuilder.append("Based on the note and the chat history, answer the last user message.");

        return promptBuilder.toString();
    }

    private void checkNoteAccess(Note note, User user) {
        boolean isOwner = note.getOwner().getId().equals(user.getId());
        boolean isCollaborator = note.getCollaborators().stream().anyMatch(c -> c.getId().equals(user.getId()));

        if (!isOwner && !isCollaborator) {
            throw new AccessDeniedException("User does not have access to this note.");
        }
    }
}