package com.example.demo.service.ai;

import com.example.demo.model.AiChatMessage;
import com.example.demo.model.AiMessageRole;
import com.example.demo.model.Note;
import com.example.demo.repository.AiChatMessageRepository;
import com.example.demo.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageSaverService {

    private final NoteRepository noteRepository;
    private final AiChatMessageRepository chatMessageRepository;

    public ChatMessageSaverService(NoteRepository noteRepository, AiChatMessageRepository chatMessageRepository) {
        this.noteRepository = noteRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMessage(Long noteId, String content, AiMessageRole role) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with ID: " + noteId));
        AiChatMessage chatMessage = new AiChatMessage();
        chatMessage.setNote(note);
        chatMessage.setContent(content);
        chatMessage.setRole(role);
        chatMessageRepository.save(chatMessage);
    }
}
