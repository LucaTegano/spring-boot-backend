package com.example.demo.service.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Service
public class GeminiService {
    private final Client client;

    public GeminiService(@Value("${google.api.key}") String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
    }

    public String askGemini(String promt) {

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.0-flash",
                promt,
                null);
        return response.text();
    }
}
