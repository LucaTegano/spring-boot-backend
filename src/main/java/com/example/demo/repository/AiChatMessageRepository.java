// src/main/java/com/example/demo/repository/AiChatMessageRepository.java
package com.example.demo.repository;

import com.example.demo.model.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    // Find all messages for a specific note, ordered by creation time
    List<AiChatMessage> findByNoteIdOrderByCreatedAtAsc(Long noteId);
    
    // Find the most recent messages for context (e.g., last 10)
    List<AiChatMessage> findTop10ByNoteIdOrderByCreatedAtDesc(Long noteId);
}