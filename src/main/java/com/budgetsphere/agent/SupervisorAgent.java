package com.budgetsphere.agent;

import com.budgetsphere.dto.AgentRequest;
import com.budgetsphere.dto.AgentResponse;
import com.budgetsphere.memory.MemoryOrchestrationService;
import com.budgetsphere.model.EpisodicMemory;
import com.budgetsphere.model.MemoryContext;
import com.budgetsphere.model.ProceduralMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Supervisor Agent - intelligent routing and orchestration
 * Determines the best agent to handle each request based on content analysis and memory context
 */
@Component
@Slf4j
public class SupervisorAgent extends BaseAgent {

    private final List<BaseAgent> availableAgents;

    public SupervisorAgent(ChatClient chatClient, 
                          MemoryOrchestrationService memoryService,
                          ReactAgent reactAgent,
                          PlanningAgent planningAgent,
                          ReflectionAgent reflectionAgent) {
        super(chatClient, memoryService);
        this.availableAgents = List.of(reactAgent, planningAgent, reflectionAgent);
    }

    @Override
    public String getAgentName() {
        return "SupervisorAgent";
    }

    @Override
    public String getAgentDescription() {
        return "Intelligent routing agent that determines the best specialized agent for each request";
    }

    @Override
    public boolean canHandle(AgentRequest request) {
        return true; // Supervisor can handle any request by routing it
    }

    @Override
    public double getConfidenceScore(AgentRequest request) {
        return 1.0; // Always confident in routing capability
    }

    @Override
    protected AgentResponse processWithMemory(AgentRequest request, MemoryContext memoryContext) {
        log.info("SupervisorAgent routing request: {}", request.getMessage());
        
        try {
            // If user specified a preferred agent, try to use it
            if (request.getPreferredAgent() != null) {
                BaseAgent preferredAgent = findAgentByName(request.getPreferredAgent());
                if (preferredAgent != null && preferredAgent.canHandle(request)) {
                    log.info("Using user-preferred agent: {}", request.getPreferredAgent());
                    AgentResponse response = preferredAgent.processRequest(request);
                    response.setRoutingInfo(
                        "User-specified agent: " + request.getPreferredAgent(),
                        1.0,
                        getAlternativeAgents(preferredAgent)
                    );
                    return response;
                }
            }
            
            // Intelligent routing based on request analysis
            BaseAgent selectedAgent = selectBestAgent(request, memoryContext);
            
            if (selectedAgent == null) {
                return AgentResponse.error(
                    request.getSessionId(),
                    request.getUserId(),
                    getAgentName(),
                    "No suitable agent found for this request",
                    "Agent selection failed"
                );
            }
            
            log.info("Routing to agent: {}", selectedAgent.getAgentName());
            
            // Process with selected agent
            AgentResponse response = selectedAgent.processRequest(request);
            
            // Add routing information
            response.setRoutingInfo(
                getRoutingReason(selectedAgent, request, memoryContext),
                selectedAgent.getConfidenceScore(request),
                getAlternativeAgents(selectedAgent)
            );
            
            return response;
            
        } catch (Exception e) {
            log.error("Error in SupervisorAgent routing: {}", e.getMessage(), e);
            return AgentResponse.error(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                "Error occurred during agent routing: " + e.getMessage(),
                "Routing exception: " + e.getClass().getSimpleName()
            );
        }
    }

    /**
     * Selects the best agent for the request using multiple criteria
     */
    private BaseAgent selectBestAgent(AgentRequest request, MemoryContext memoryContext) {
        Map<BaseAgent, Double> agentScores = new HashMap<>();
        
        for (BaseAgent agent : availableAgents) {
            if (agent.canHandle(request)) {
                double score = calculateAgentScore(agent, request, memoryContext);
                agentScores.put(agent, score);
                log.debug("Agent {} score: {}", agent.getAgentName(), score);
            }
        }
        
        if (agentScores.isEmpty()) {
            return null;
        }
        
        // Return agent with highest score
        return agentScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * Calculates a comprehensive score for an agent's suitability
     */
    private double calculateAgentScore(BaseAgent agent, AgentRequest request, MemoryContext memoryContext) {
        double score = 0.0;
        
        // Base confidence from agent
        score += agent.getConfidenceScore(request) * 0.4;
        
        // Request type analysis
        score += analyzeRequestTypeMatch(agent, request) * 0.3;
        
        // Memory context analysis
        if (memoryContext != null) {
            score += analyzeMemoryContextMatch(agent, memoryContext) * 0.2;
        }
        
        // Historical performance
        score += analyzeHistoricalPerformance(agent, request, memoryContext) * 0.1;
        
        return Math.min(1.0, score);
    }

    /**
     * Analyzes how well the agent matches the request type
     */
    private double analyzeRequestTypeMatch(BaseAgent agent, AgentRequest request) {
        String agentName = agent.getAgentName();
        String message = request.getMessage().toLowerCase();

        return switch (agentName) {
            case "ReactAgent" -> {
                // Good for immediate questions, calculations, quick advice
                if (message.contains("how much") ||
                        message.contains("calculate") ||
                        message.contains("what is") ||
                        message.contains("quick") ||
                        message.contains("simple")) {
                    yield 0.9;
                }
                yield 0.6;
            }
            case "PlanningAgent" -> {
                // Good for complex planning, strategies, long-term goals
                if (request.isComplexPlanningRequest() ||
                        message.contains("plan") ||
                        message.contains("strategy") ||
                        message.contains("long-term") ||
                        message.contains("retirement") ||
                        message.contains("investment portfolio")) {
                    yield 0.9;
                }
                yield 0.3;
            }
            case "ReflectionAgent" -> {
                // Good for analysis, reviews, performance evaluation
                if (request.isReflectionRequest() ||
                        message.contains("analyze") ||
                        message.contains("review") ||
                        message.contains("performance") ||
                        message.contains("progress")) {
                    yield 0.9;
                }
                yield 0.2;
            }
            default -> 0.5;
        };
    }

    /**
     * Analyzes how well the agent matches the memory context
     */
    private double analyzeMemoryContextMatch(BaseAgent agent, MemoryContext memoryContext) {
        if (!memoryContext.hasSufficientContext()) {
            return 0.5; // Neutral if no strong memory context
        }
        
        String agentName = agent.getAgentName();
        
        // Check if this agent has been successful in similar contexts
        long successfulEpisodes = memoryContext.getEpisodicMemories().stream()
            .filter(e -> e.getAgentName().equals(agentName))
            .filter(EpisodicMemory::isSuccessful)
            .count();
        
        long totalEpisodes = memoryContext.getEpisodicMemories().stream()
            .filter(e -> e.getAgentName().equals(agentName))
            .count();
        
        if (totalEpisodes > 0) {
            return (double) successfulEpisodes / totalEpisodes;
        }
        
        // Check if available procedures match this agent
        long matchingProcedures = memoryContext.getProcedures().stream()
            .filter(p -> p.getAgentPattern().equals(agentName))
            .filter(ProceduralMemory::isEffective)
            .count();
        
        return Math.min(1.0, matchingProcedures * 0.2);
    }

    /**
     * Analyzes historical performance of the agent
     */
    private double analyzeHistoricalPerformance(BaseAgent agent, AgentRequest request, MemoryContext memoryContext) {
        if (memoryContext == null || memoryContext.getEpisodicMemories().isEmpty()) {
            return 0.5; // Neutral if no history
        }
        
        String agentName = agent.getAgentName();
        String topic = request.getEffectiveConversationTopic();
        
        // Calculate success rate for this agent on this topic
        List<Double> satisfactionScores = memoryContext.getEpisodicMemories().stream()
            .filter(e -> e.getAgentName().equals(agentName))
            .filter(e -> topic.equals(e.getConversationTopic()))
            .map(EpisodicMemory::getSatisfactionScore)
            .filter(Objects::nonNull)
            .toList();
        
        if (!satisfactionScores.isEmpty()) {
            return satisfactionScores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.5);
        }
        
        return 0.5;
    }

    /**
     * Finds an agent by name
     */
    private BaseAgent findAgentByName(String agentName) {
        return availableAgents.stream()
            .filter(agent -> agent.getAgentName().equals(agentName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets alternative agents for the response
     */
    private List<String> getAlternativeAgents(BaseAgent selectedAgent) {
        return availableAgents.stream()
            .map(BaseAgent::getAgentName)
            .filter(agentName -> !agentName.equals(selectedAgent.getAgentName()))
            .collect(Collectors.toList());
    }

    /**
     * Gets the reasoning for agent selection
     */
    private String getRoutingReason(BaseAgent selectedAgent, AgentRequest request, MemoryContext memoryContext) {
        StringBuilder reason = new StringBuilder();
        
        reason.append("Selected ").append(selectedAgent.getAgentName()).append(" because: ");
        
        String agentName = selectedAgent.getAgentName();
        String message = request.getMessage().toLowerCase();
        
        switch (agentName) {
            case "ReactAgent":
                if (message.contains("calculate") || message.contains("how much")) {
                    reason.append("request requires immediate calculation or quick response");
                } else {
                    reason.append("suitable for general financial questions");
                }
                break;
                
            case "PlanningAgent":
                if (request.isComplexPlanningRequest()) {
                    reason.append("request involves complex financial planning");
                } else {
                    reason.append("strategic planning capabilities needed");
                }
                break;
                
            case "ReflectionAgent":
                if (request.isReflectionRequest()) {
                    reason.append("request requires analysis and reflection");
                } else {
                    reason.append("analytical capabilities needed");
                }
                break;
                
            default:
                reason.append("best match based on content analysis");
        }
        
        // Add memory context influence
        if (memoryContext != null && memoryContext.hasSufficientContext()) {
            reason.append("; memory context supports this choice");
        }
        
        return reason.toString();
    }

    @Override
    protected String buildSystemPrompt(AgentRequest request, MemoryContext memoryContext) {
        return """
            You are the SupervisorAgent for BudgetSphere, responsible for intelligent routing of user requests.
            Your role is to analyze requests and determine the most appropriate specialized agent.
            
            Available agents:
            - ReactAgent: Handles immediate questions, calculations, and quick financial advice
            - PlanningAgent: Handles complex financial planning, strategies, and long-term goals
            - ReflectionAgent: Handles analysis, reviews, and performance evaluations
            
            This prompt should not be used directly as you route to other agents.
            """;
    }
}

