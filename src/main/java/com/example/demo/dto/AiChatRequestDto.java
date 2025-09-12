// src/main/java/com/example/demo/dto/AiChatRequestDto.java
package com.example.demo.dto;

// Correct import for Spring Boot 3+
import jakarta.validation.constraints.NotBlank;
import lombok.Data; 

@Data
public class AiChatRequestDto {
    @NotBlank
    private String message;
}
