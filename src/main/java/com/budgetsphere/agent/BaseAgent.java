package com.budgetsphere.agent;

import com.budgetsphere.dto.AgentRequest;
import com.budgetsphere.dto.AgentResponse;
import com.budgetsphere.memory.MemoryOrchestrationService;
import com.budgetsphere.model.MemoryContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base Agent class - provides common functionality for all agents
 * Handles memory integration, prompt construction, and response processing
 */
@RequiredArgsConstructor
@Slf4j
public abstract class BaseAgent {

    protected final ChatClient chatClient;
    protected final MemoryOrchestrationService memoryService;

    /**
     * Processes a request using the agent's specific logic
     */
    public AgentResponse processRequest(AgentRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Processing request with {}: {}", getAgentName(), request.getMessage());
            
            // Validate request
            request.validate();
            
            // Build memory context if enabled
            MemoryContext memoryContext = null;
            if (request.getUseMemory()) {
                memoryContext = memoryService.buildMemoryContext(
                    request.getSessionId(),
                    request.getUserId(),
                    request.getMessage(),
                    request.getContext(),
                    getAgentName()
                );
            }
            
            // Process with agent-specific logic
            AgentResponse response = processWithMemory(request, memoryContext);
            
            // Set processing time
            long processingTime = System.currentTimeMillis() - startTime;
            response.setProcessingTimeMs(processingTime);
            response.setTimestamp(LocalDateTime.now());
            
            // Store experience if enabled
            if (request.getStoreExperience() && response.getSuccess()) {
                storeExperience(request, response, memoryContext);
            }
            
            // Update previous feedback if provided
            if (request.getUserFeedback() != null || request.getPreviousSatisfaction() != null) {
                memoryService.updateLatestEpisodeFeedback(
                    request.getSessionId(),
                    request.getUserFeedback(),
                    request.getPreviousSatisfaction()
                );
            }
            
            log.info("Successfully processed request with {} in {}ms", getAgentName(), processingTime);
            return response;
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Error processing request with {}: {}", getAgentName(), e.getMessage(), e);
            
            return AgentResponse.error(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                "An error occurred while processing your request: " + e.getMessage(),
                "Exception during processing: " + e.getClass().getSimpleName()
            );
        }
    }

    /**
     * Abstract method for agent-specific processing with memory context
     */
    protected abstract AgentResponse processWithMemory(AgentRequest request, MemoryContext memoryContext);

    /**
     * Gets the agent's name/identifier
     */
    public abstract String getAgentName();

    /**
     * Gets the agent's description
     */
    public abstract String getAgentDescription();

    /**
     * Checks if this agent can handle the given request
     */
    public abstract boolean canHandle(AgentRequest request);

    /**
     * Gets the agent's confidence score for handling the request (0.0 to 1.0)
     */
    public abstract double getConfidenceScore(AgentRequest request);

    /**
     * Builds the system prompt for this agent
     */
    protected abstract String buildSystemPrompt(AgentRequest request, MemoryContext memoryContext);

    /**
     * Builds the user prompt incorporating memory context
     */
    protected String buildUserPrompt(AgentRequest request, MemoryContext memoryContext) {
        StringBuilder prompt = new StringBuilder();
        
        // Add memory context if available
        if (memoryContext != null && memoryContext.hasSufficientContext()) {
            prompt.append("MEMORY CONTEXT:\n");
            prompt.append(memoryContext.getAgentContext());
            prompt.append("\n");
        }
        
        // Add current request context
        if (request.getContext() != null && !request.getContext().isEmpty()) {
            prompt.append("CURRENT CONTEXT:\n");
            request.getContext().forEach((key, value) -> 
                prompt.append("- ").append(key).append(": ").append(value).append("\n"));
            prompt.append("\n");
        }
        
        // Add the main message
        prompt.append("USER REQUEST:\n");
        prompt.append(request.getMessage());
        
        return prompt.toString();
    }

    /**
     * Executes the chat request with the LLM
     */
    protected AgentResponse executeChatRequest(AgentRequest request, MemoryContext memoryContext) {
        try {
            String systemPrompt = buildSystemPrompt(request, memoryContext);
            String userPrompt = buildUserPrompt(request, memoryContext);
            
            log.debug("System prompt for {}: {}", getAgentName(), systemPrompt);
            log.debug("User prompt for {}: {}", getAgentName(), userPrompt);
            
            Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
            ));
            
            ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
            assert chatResponse != null;
            String responseMessage = chatResponse.getResult().getOutput().getText();
            
            // Build response
            AgentResponse response = AgentResponse.success(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                responseMessage,
                "Generated response using " + getAgentName() + " with memory context",
                "chat_response"
            );
            
            // Add memory information
            if (memoryContext != null) {
                response.setMemoryInfo(
                    memoryContext.getConfidenceScore(),
                    memoryContext.getSummary()
                );
                
                // Add memory-derived recommendations
                List<String> recommendations = memoryContext.getRecommendations();
                recommendations.forEach(response::addRecommendation);
                
                // Add relevant experiences
                memoryContext.getEpisodicMemories().stream()
                    .limit(3)
                    .forEach(episode -> response.addRelevantExperience(episode.getSummary()));
                
                // Add applied knowledge
                memoryContext.getSemanticKnowledge().stream()
                    .limit(3)
                    .forEach(knowledge -> response.addAppliedKnowledge(knowledge.getSummary()));
                
                // Add used procedures
                memoryContext.getProcedures().stream()
                    .limit(3)
                    .forEach(procedure -> response.addUsedProcedure(procedure.getSummary()));
            }
            
            // Add agent-specific metadata
            response.addMetadata("model", "qwen2.5:7b");
            response.addMetadata("temperature", 0.1);
            response.addMetadata("memoryEnabled", request.getUseMemory());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error executing chat request with {}: {}", getAgentName(), e.getMessage(), e);
            throw new RuntimeException("Failed to execute chat request", e);
        }
    }

    /**
     * Stores the experience in memory
     */
    protected void storeExperience(AgentRequest request, AgentResponse response, MemoryContext memoryContext) {
        try {
            memoryService.storeExperience(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                getAgentName(), // agent pattern
                request.getMessage(),
                request.getContext(),
                response.getMessage(),
                response.getSuccess(),
                response.getReasoning(),
                response.getActionTaken()
            );
            
            log.debug("Stored experience for user: {}, agent: {}", request.getUserId(), getAgentName());
            
        } catch (Exception e) {
            log.error("Error storing experience for user: {}, agent: {}", request.getUserId(), getAgentName(), e);
        }
    }

    /**
     * Extracts financial context from request
     */
    protected Map<String, Object> extractFinancialContext(AgentRequest request) {
        Map<String, Object> financialContext = request.getContext();
        
        // Add derived financial metrics
        if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
            double savingsRate = (request.getMonthlyIncome() - request.getMonthlyExpenses()) / request.getMonthlyIncome();
            financialContext.put("savingsRate", savingsRate);
            financialContext.put("disposableIncome", request.getMonthlyIncome() - request.getMonthlyExpenses());
        }
        
        return financialContext;
    }

    /**
     * Validates that required financial context is present
     */
    protected boolean hasRequiredFinancialContext(AgentRequest request) {
        return request.getMonthlyIncome() != null || 
               request.getMonthlyExpenses() != null ||
               request.getGoal() != null ||
               !request.getContext().isEmpty();
    }

    /**
     * Gets common financial planning recommendations
     */
    protected List<String> getCommonRecommendations(AgentRequest request) {
        List<String> recommendations = new ArrayList<>();
        
        if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
            double savingsRate = (request.getMonthlyIncome() - request.getMonthlyExpenses()) / request.getMonthlyIncome();
            
            if (savingsRate < 0.1) {
                recommendations.add("Consider increasing your savings rate to at least 10% of income");
            }
            if (savingsRate > 0.3) {
                recommendations.add("Excellent savings rate! Consider investing excess savings for long-term growth");
            }
        }
        
        if (request.getAge() != null && request.getAge() < 30) {
            recommendations.add("Start investing early to take advantage of compound growth");
        }
        
        if ("HIGH".equals(request.getRiskTolerance())) {
            recommendations.add("Consider growth-oriented investments given your high risk tolerance");
        } else if ("LOW".equals(request.getRiskTolerance())) {
            recommendations.add("Focus on conservative investments and emergency fund building");
        }
        
        return recommendations;
    }
}

