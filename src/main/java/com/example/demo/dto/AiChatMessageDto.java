package com.example.demo.dto;

import com.example.demo.model.AiMessageRole;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AiChatMessageDto {
    private Long id;
    private AiMessageRole role;
    private String content;
    private LocalDateTime createdAt;
}
