package com.example.demo.service.ai;

import com.example.demo.dto.AiChatMessageDto;
import com.example.demo.model.*;
import com.example.demo.repository.AiChatMessageRepository;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
// This is the corrected import
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImpl implements AiChatService {

    private final NoteRepository noteRepository;
    private final AiChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ChatMessageSaverService chatMessageSaverService;

    public AiChatServiceImpl(NoteRepository noteRepository, AiChatMessageRepository chatMessageRepository,
            UserRepository userRepository, GeminiService geminiService,
            ChatMessageSaverService chatMessageSaverService) {
        this.noteRepository = noteRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.chatMessageSaverService = chatMessageSaverService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiChatMessageDto> getChatHistory(Long noteId) {
        Note note = checkNoteAccess(noteId);
        return chatMessageRepository.findByNoteIdOrderByCreatedAtAsc(note.getId())
                .stream()
                .map(msg -> new AiChatMessageDto(msg.getId(), msg.getRole(), msg.getContent(), msg.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public StreamingResponseBody streamChatResponse(Long noteId, String userMessage) {
        // Perform security check and save user message in the main thread
        Note note = checkNoteAccess(noteId);
        chatMessageSaverService.saveMessage(noteId, userMessage, AiMessageRole.USER);

        List<AiChatMessage> history = chatMessageRepository.findTop10ByNoteIdOrderByCreatedAtDesc(noteId);
        Collections.reverse(history);

        String prompt = buildPrompt(note, history);

        System.out.println("================ PROMPT SENT TO GEMINI ================");
        System.out.println(prompt);
        System.out.println("=====================================================");

        // Capture the security context from the current thread
        final SecurityContext securityContext = SecurityContextHolder.getContext();

        return outputStream -> {
            // Define the core logic as a Runnable
            Runnable task = () -> {
                StringBuilder fullResponse = new StringBuilder();
                try {
                    String geminiResponse = geminiService.askGemini(prompt);

                    for (char c : geminiResponse.toCharArray()) {
                        outputStream.write(String.valueOf(c).getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        Thread.sleep(5);
                    }
                    fullResponse.append(geminiResponse);

                    // This now runs with the propagated security context
                    chatMessageSaverService.saveMessage(noteId, fullResponse.toString(), AiMessageRole.MODEL);

                } catch (Exception e) {
                    System.err.println("Error while streaming chat response: " + e.getMessage());
                    try {
                        String errorMsg = "Sorry, an error occurred while processing your request.";
                        outputStream.write(errorMsg.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    } catch (IOException ioEx) {
                        // Ignore
                    }
                } finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            };

            // Wrap the task with the captured security context and run it
            new DelegatingSecurityContextRunnable(task, securityContext).run();
        };
    }

    private String buildPrompt(Note note, List<AiChatMessage> history) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("You are a helpful Q&A assistant for a note-taking app. Follow these strict rules:\n");
        promptBuilder.append(
                "1. Your primary context is a single note. It will be provided ONLY ONCE at the start of the conversation.\n");
        promptBuilder.append("2. Your main goal is to answer the user's latest question concisely.\n");
        promptBuilder
                .append("5. Base your answers ONLY on the note's content and the direct conversation history.\n\n");

        boolean isFirstTurn = history.stream().filter(m -> m.getRole() == AiMessageRole.MODEL).findAny().isEmpty();

        if (isFirstTurn) {
            promptBuilder.append("--- NOTE START ---\n");
            promptBuilder.append("Title: ").append(note.getTitle()).append("\n");
            promptBuilder.append("Content: ").append(note.getContent()).append("\n");
            promptBuilder.append("--- NOTE END ---\n");
        }

        for (AiChatMessage message : history) {
            promptBuilder.append(message.getRole().name()).append(": ").append(message.getContent()).append("\n");
        }
        promptBuilder.append("--- CHAT HISTORY END ---\n");

        promptBuilder.append("Now, following all the rules above, answer the last USER message.\n");
        promptBuilder.append("MODEL: ");

        return promptBuilder.toString();
    }

    @Transactional(readOnly = true)
    public Note checkNoteAccess(Long noteId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));

        note.getOwner().getId();
        note.getCollaborators().size();

        boolean isOwner = note.getOwner().getId().equals(currentUser.getId());
        boolean isCollaborator = note.getCollaborators().stream().anyMatch(c -> c.getId().equals(currentUser.getId()));

        if (!isOwner && !isCollaborator) {
            throw new SecurityException("User does not have access to this note.");
        }
        return note;
    }
}