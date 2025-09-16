// src/main/java/com/example/demo/dto/AiChatRequestDto.java
package com.example.demo.dto;

// Correct import for Spring Boot 3+
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequestDto {
    @NotBlank(message = "Message cannot be empty")
    private String message;
}
