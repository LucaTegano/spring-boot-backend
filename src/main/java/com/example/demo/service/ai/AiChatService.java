// src/main/java/com/example/demo/service/AiChatService.java
package com.example.demo.service.ai;

import com.example.demo.dto.AiChatMessageDto;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.util.List;

public interface AiChatService {

    List<AiChatMessageDto> getChatHistory(Long noteId);

    StreamingResponseBody streamChatResponse(Long noteId, String userMessage);
}