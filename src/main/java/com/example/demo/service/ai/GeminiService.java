package com.example.demo.service.ai;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final Client client = Client.builder().apiKey("AIzaSyAj9C1b2fB4CtnetLAEnhIFWzpZAK_s5Ss").build();

    public String askGemini(String promt) {

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.0-flash",
                promt,
                null);
        return response.text();
    }

    public static void main(String args[]) {
        GeminiService geminiService = new GeminiService();
        System.out.println(geminiService.askGemini("CIAO"));
    }
}
