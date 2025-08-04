package com.budgetsphere.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class SpringAIConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.model}")
    private String defaultModel;

    @Bean
    @Primary
    public ChatModel chatModel(
            ToolCallingManager toolCallingManager,
            ObservationRegistry observationRegistry,
            ModelManagementOptions modelManagementOptions) {
        return new OllamaChatModel(
                new OllamaApi(ollamaBaseUrl),
                OllamaOptions.builder()
                        .model(defaultModel)
                        .temperature(0.7)
                        .topP(0.9)
                        .numPredict(1000)
                        .build(),
                toolCallingManager,
                observationRegistry,
                modelManagementOptions);
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    Você é um assistente financeiro especializado em orçamento pessoal.
                    Seja prático, objetivo e forneça conselhos financeiros sólidos.
                    Use linguagem clara e acessível.
                    Sempre considere a situação financeira brasileira.
                    """)
                .build();
    }

    @Bean
    public ModelManagementOptions modelManagementOptions() {
        return ModelManagementOptions.builder()
                .pullModelStrategy(PullModelStrategy.WHEN_MISSING)
                .timeout(Duration.ofMinutes(5))
                .maxRetries(1)
                .build();
    }
}
