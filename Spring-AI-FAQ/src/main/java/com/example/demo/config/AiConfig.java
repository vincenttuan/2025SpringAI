package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

@Configuration
public class AiConfig {

    private static final String OLLAMA_URL = "http://localhost:11434";
    private static final String MODEL = "llama3.1:8b";

    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder()
                .baseUrl(OLLAMA_URL)
                .build();
    }

    @Bean
    public OllamaChatModel chatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model(MODEL)
                                .build()
                )
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(100)
                .build();
    }
}
