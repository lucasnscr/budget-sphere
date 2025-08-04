package com.budgetsphere.agent;

import com.budgetsphere.model.AgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe base para todos os agentes do BudgetSphere
 * Fornece funcionalidades comuns e estrutura para implementação de patterns
 */
@Slf4j
public abstract class BaseAgent {
    
    @Autowired
    protected ChatClient chatClient;
    
    protected final String agentName;
    protected final String pattern;
    
    public BaseAgent(String agentName, String pattern) {
        this.agentName = agentName;
        this.pattern = pattern;
    }
    
    /**
     * Método principal que cada agente deve implementar
     */
    public abstract AgentResponse process(String input, Map<String, Object> context);
    
    /**
     * Cria uma resposta padrão do agente
     */
    protected AgentResponse createResponse(String message, Map<String, Object> data, boolean success) {
        return AgentResponse.builder()
                .agentName(agentName)
                .pattern(pattern)
                .message(message)
                .data(data != null ? data : new HashMap<>())
                .success(success)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Cria uma resposta com raciocínio (para patterns como ReAct)
     */
    protected AgentResponse createResponseWithReasoning(String message, Map<String, Object> data, 
                                                       String reasoning, String nextAction, boolean success) {
        return AgentResponse.builder()
                .agentName(agentName)
                .pattern(pattern)
                .message(message)
                .data(data != null ? data : new HashMap<>())
                .reasoning(reasoning)
                .nextAction(nextAction)
                .success(success)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Chama o modelo de IA com um prompt
     */
    protected String callAI(String prompt) {
        try {
            log.debug("🤖 {} chamando IA com prompt: {}", agentName, prompt.substring(0, Math.min(100, prompt.length())));
            return chatClient.prompt(prompt).call().content();
        } catch (Exception e) {
            log.error("❌ Erro ao chamar IA no agente {}: {}", agentName, e.getMessage());
            return "Erro ao processar solicitação: " + e.getMessage();
        }
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public String getPattern() {
        return pattern;
    }
}

