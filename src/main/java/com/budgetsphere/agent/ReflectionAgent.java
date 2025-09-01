package com.budgetsphere.agent;

import com.budgetsphere.dto.AgentRequest;
import com.budgetsphere.dto.AgentResponse;
import com.budgetsphere.memory.MemoryOrchestrationService;
import com.budgetsphere.model.MemoryContext;
import com.budgetsphere.model.EpisodicMemory;
import com.budgetsphere.model.ProceduralMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reflection Agent - handles analysis, performance review, and insights
 * Specializes in analyzing past financial decisions, progress tracking, and providing insights
 */
@Component
@Slf4j
public class ReflectionAgent extends BaseAgent {

    public ReflectionAgent(ChatClient chatClient, MemoryOrchestrationService memoryService) {
        super(chatClient, memoryService);
    }

    @Override
    public String getAgentName() {
        return "ReflectionAgent";
    }

    @Override
    public String getAgentDescription() {
        return "Analytical agent for financial performance review, progress analysis, and insights generation";
    }

    @Override
    public boolean canHandle(AgentRequest request) {
        // ReflectionAgent is best for analysis and review requests
        return request.isReflectionRequest() || 
               request.getMessage().toLowerCase().contains("analyze") ||
               request.getMessage().toLowerCase().contains("review") ||
               request.getMessage().toLowerCase().contains("progress");
    }

    @Override
    public double getConfidenceScore(AgentRequest request) {
        String message = request.getMessage().toLowerCase();
        
        // High confidence for reflection and analysis requests
        if (request.isReflectionRequest()) {
            return 0.9;
        }
        
        if (message.contains("analyze") || 
            message.contains("review") || 
            message.contains("performance") ||
            message.contains("progress") ||
            message.contains("how am i doing") ||
            message.contains("summary") ||
            message.contains("analisar") ||
            message.contains("revisar") ||
            message.contains("progresso") ||
            message.contains("como estou")) {
            return 0.8;
        }
        
        // Medium confidence for evaluation requests
        if (message.contains("evaluate") || 
            message.contains("assess") ||
            message.contains("compare") ||
            message.contains("avaliar") ||
            message.contains("comparar")) {
            return 0.7;
        }
        
        // Lower confidence for planning or calculation requests
        if (message.contains("plan") || 
            message.contains("calculate") ||
            message.contains("planejar") ||
            message.contains("calcular")) {
            return 0.2;
        }
        
        return 0.3; // Default confidence
    }

    @Override
    protected AgentResponse processWithMemory(AgentRequest request, MemoryContext memoryContext) {
        log.info("ReflectionAgent processing request: {}", request.getMessage());
        
        try {
            // Execute the chat request with reflection-focused prompt
            AgentResponse response = executeChatRequest(request, memoryContext);
            
            // Add reflection-specific analysis
            addReflectionAnalysis(response, request, memoryContext);
            
            // Add insights and recommendations
            addInsightsAndRecommendations(response, request, memoryContext);
            
            // Add analytical next steps
            addAnalyticalNextSteps(response, request);
            
            // Add performance analysis if memory context is available
            if (memoryContext != null && memoryContext.hasSufficientContext()) {
                addPerformanceAnalysis(response, request, memoryContext);
            }
            
            response.setActionTaken("reflection_analysis");
            
            return response;
            
        } catch (Exception e) {
            log.error("Error in ReflectionAgent processing: {}", e.getMessage(), e);
            return AgentResponse.error(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                "Error analyzing your financial situation: " + e.getMessage(),
                "ReflectionAgent processing error"
            );
        }
    }

    @Override
    protected String buildSystemPrompt(AgentRequest request, MemoryContext memoryContext) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("""
            You are ReflectionAgent, a specialized financial analyst focused on providing deep insights,\s
            performance analysis, and reflective guidance on financial decisions and progress.
           \s
            Your role:
            - Analyze financial performance and progress toward goals
            - Provide insights based on historical data and patterns
            - Identify trends, strengths, and areas for improvement
            - Offer reflective guidance on financial decisions
            - Compare current situation with past performance
            - Generate actionable insights for future improvement
           \s
            Guidelines:
            - Be analytical and data-driven in your assessments
            - Provide specific, measurable insights
            - Identify both positive trends and areas needing attention
            - Use historical context to provide perspective
            - Be encouraging while being honest about challenges
            - Provide actionable recommendations based on analysis
            - Use Brazilian Portuguese if the user writes in Portuguese
           \s
            Analysis Framework:
            1. Current situation assessment
            2. Historical trend analysis
            3. Goal progress evaluation
            4. Pattern identification
            5. Strength and weakness analysis
            6. Future improvement recommendations
           \s
           \s""");
        
        // Add current financial context
        if (hasRequiredFinancialContext(request)) {
            prompt.append("Current Financial Snapshot:\n");
            if (request.getMonthlyIncome() != null) {
                prompt.append("- Monthly Income: R$ ").append(String.format("%.2f", request.getMonthlyIncome())).append("\n");
            }
            if (request.getMonthlyExpenses() != null) {
                prompt.append("- Monthly Expenses: R$ ").append(String.format("%.2f", request.getMonthlyExpenses())).append("\n");
            }
            if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
                double savingsRate = (request.getMonthlyIncome() - request.getMonthlyExpenses()) / request.getMonthlyIncome();
                prompt.append("- Current Savings Rate: ").append(String.format("%.1f%%", savingsRate * 100)).append("\n");
            }
            if (request.getGoal() != null) {
                prompt.append("- Primary Goal: ").append(request.getGoal()).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add comprehensive historical analysis
        if (memoryContext != null && memoryContext.hasSufficientContext()) {
            prompt.append("Historical Analysis Data:\n");
            
            // Episode analysis
            if (!memoryContext.getEpisodicMemories().isEmpty()) {
                List<EpisodicMemory> episodes = memoryContext.getEpisodicMemories();
                
                long totalInteractions = episodes.size();
                long successfulInteractions = episodes.stream()
                    .mapToLong(e -> e.isSuccessful() ? 1 : 0)
                    .sum();
                
                double avgSatisfaction = episodes.stream()
                    .filter(e -> e.getSatisfactionScore() != null)
                    .mapToDouble(EpisodicMemory::getSatisfactionScore)
                    .average()
                    .orElse(0.0);
                
                prompt.append("Interaction History:\n");
                prompt.append("- Total Interactions: ").append(totalInteractions).append("\n");
                prompt.append("- Successful Interactions: ").append(successfulInteractions)
                      .append(" (").append(String.format("%.1f%%", (double) successfulInteractions / totalInteractions * 100)).append(")\n");
                prompt.append("- Average Satisfaction: ").append(String.format("%.2f", avgSatisfaction)).append("/1.0\n");
                
                // Topic analysis
                Map<String, Long> topicCounts = episodes.stream()
                    .filter(e -> e.getConversationTopic() != null)
                    .collect(Collectors.groupingBy(EpisodicMemory::getConversationTopic, Collectors.counting()));
                
                if (!topicCounts.isEmpty()) {
                    prompt.append("Most Discussed Topics:\n");
                    topicCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .forEach(entry -> 
                            prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" times\n"));
                }
                
                // Recent trends
                LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
                long recentInteractions = episodes.stream()
                    .filter(e -> e.getTimestamp().isAfter(weekAgo))
                    .count();
                
                if (recentInteractions > 0) {
                    prompt.append("Recent Activity (Last 7 days): ").append(recentInteractions).append(" interactions\n");
                }
                
                prompt.append("\n");
            }
            
            // Success patterns
            if (!memoryContext.getSuccessPatterns().isEmpty()) {
                prompt.append("Identified Success Patterns:\n");
                memoryContext.getSuccessPatterns().forEach(pattern -> 
                    prompt.append("- ").append(pattern).append("\n"));
                prompt.append("\n");
            }
            
            // Knowledge application
            if (!memoryContext.getSemanticKnowledge().isEmpty()) {
                prompt.append("Applied Financial Knowledge:\n");
                memoryContext.getSemanticKnowledge().stream()
                    .limit(3)
                    .forEach(knowledge -> 
                        prompt.append("- ").append(knowledge.getConcept())
                              .append(" (Used ").append(knowledge.getUsageCount()).append(" times, ")
                              .append(String.format("%.1f%%", (knowledge.getSuccessRate() != null ? knowledge.getSuccessRate() : 0.0) * 100))
                              .append(" success rate)\n"));
                prompt.append("\n");
            }
            
            // Procedure effectiveness
            if (!memoryContext.getProcedures().isEmpty()) {
                prompt.append("Procedure Performance:\n");
                memoryContext.getProcedures().stream()
                    .limit(3)
                    .forEach(procedure -> 
                        prompt.append("- ").append(procedure.getProcedureName())
                              .append(": ").append(String.format("%.1f%%", procedure.getSuccessRate() * 100))
                              .append(" success rate, ")
                              .append(String.format("%.2f", procedure.getAverageSatisfactionScore() != null ? procedure.getAverageSatisfactionScore() : 0.0))
                              .append(" avg satisfaction\n"));
                prompt.append("\n");
            }
        }
        
        prompt.append("""
            Provide a comprehensive analysis of the user's financial journey, highlighting key insights,\s
            trends, and recommendations for improvement. Be specific and actionable in your analysis.
           \s""");
        
        return prompt.toString();
    }

    /**
     * Adds reflection-specific analysis to the response
     */
    private void addReflectionAnalysis(AgentResponse response, AgentRequest request, MemoryContext memoryContext) {
        if (memoryContext == null || !memoryContext.hasSufficientContext()) {
            response.addMetadata("analysisScope", "limited");
            response.addMetadata("analysisNote", "Limited historical data available for comprehensive analysis");
            return;
        }
        
        // Analyze interaction patterns
        List<EpisodicMemory> episodes = memoryContext.getEpisodicMemories();
        
        if (!episodes.isEmpty()) {
            // Success rate analysis
            double successRate = episodes.stream()
                .mapToDouble(e -> e.isSuccessful() ? 1.0 : 0.0)
                .average()
                .orElse(0.0);
            
            response.addMetadata("overallSuccessRate", String.format("%.1f%%", successRate * 100));
            
            // Satisfaction trend
            double avgSatisfaction = episodes.stream()
                .filter(e -> e.getSatisfactionScore() != null)
                .mapToDouble(EpisodicMemory::getSatisfactionScore)
                .average()
                .orElse(0.0);
            
            response.addMetadata("averageSatisfaction", String.format("%.2f", avgSatisfaction));
            
            // Most effective agent
            Map<String, Double> agentSatisfaction = episodes.stream()
                .filter(e -> e.getSatisfactionScore() != null)
                .collect(Collectors.groupingBy(
                    EpisodicMemory::getAgentName,
                    Collectors.averagingDouble(EpisodicMemory::getSatisfactionScore)
                ));
            
            if (!agentSatisfaction.isEmpty()) {
                String bestAgent = agentSatisfaction.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Unknown");
                
                response.addMetadata("mostEffectiveAgent", bestAgent);
            }
            
            // Topic analysis
            Map<String, Long> topicFrequency = episodes.stream()
                .filter(e -> e.getConversationTopic() != null)
                .collect(Collectors.groupingBy(EpisodicMemory::getConversationTopic, Collectors.counting()));
            
            if (!topicFrequency.isEmpty()) {
                String mostDiscussedTopic = topicFrequency.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("general");
                
                response.addMetadata("primaryFocusArea", mostDiscussedTopic);
            }
        }
        
        response.addMetadata("analysisScope", "comprehensive");
        response.addMetadata("memoryConfidence", memoryContext.getConfidenceScore());
    }

    /**
     * Adds insights and recommendations based on analysis
     */
    private void addInsightsAndRecommendations(AgentResponse response, AgentRequest request, MemoryContext memoryContext) {
        if (memoryContext != null && memoryContext.hasSufficientContext()) {
            // Add memory-derived insights
            List<String> recommendations = memoryContext.getRecommendations();
            recommendations.forEach(response::addRecommendation);
            
            // Add pattern-based insights
            if (!memoryContext.getSuccessPatterns().isEmpty()) {
                response.addRecommendation("Continue leveraging your successful patterns: " + 
                    String.join(", ", memoryContext.getSuccessPatterns()));
            }
            
            // Add improvement opportunities based on procedures
            memoryContext.getProcedures().stream()
                .filter(ProceduralMemory::needsOptimization)
                .forEach(p -> response.addRecommendation(p.getImprovementRecommendation()));
        }
        
        // Add general reflection-based recommendations
        response.addRecommendation("Schedule regular financial reviews to maintain awareness of your progress");
        response.addRecommendation("Document your financial decisions and their outcomes for future learning");
        response.addRecommendation("Set measurable milestones to track progress toward your goals");
        
        if (hasRequiredFinancialContext(request)) {
            response.addRecommendation("Compare your current metrics with industry benchmarks and personal goals");
        }
    }

    /**
     * Adds analytical next steps
     */
    private void addAnalyticalNextSteps(AgentResponse response, AgentRequest request) {
        response.addNextStep("Review the analysis insights and identify top 3 areas for improvement");
        response.addNextStep("Set specific, measurable goals based on the identified opportunities");
        response.addNextStep("Create action plans for addressing any weaknesses highlighted in the analysis");
        
        if (hasRequiredFinancialContext(request)) {
            response.addNextStep("Track key financial metrics monthly to monitor progress");
            response.addNextStep("Compare your performance against your previous periods and goals");
        }
        
        response.addNextStep("Schedule follow-up analysis in 3-6 months to assess progress");
        response.addNextStep("Consider seeking additional guidance for areas where improvement is needed");
    }

    /**
     * Adds performance analysis based on memory context
     */
    private void addPerformanceAnalysis(AgentResponse response, AgentRequest request, MemoryContext memoryContext) {
        AgentResponse.FinancialAnalysis analysis = AgentResponse.FinancialAnalysis.builder().build();
        
        // Analyze historical performance if available
        List<EpisodicMemory> episodes = memoryContext.getEpisodicMemories();
        
        if (!episodes.isEmpty()) {
            // Calculate performance metrics
            double successRate = episodes.stream()
                .mapToDouble(e -> e.isSuccessful() ? 1.0 : 0.0)
                .average()
                .orElse(0.0);
            
            double avgSatisfaction = episodes.stream()
                .filter(e -> e.getSatisfactionScore() != null)
                .mapToDouble(EpisodicMemory::getSatisfactionScore)
                .average()
                .orElse(0.0);
            
            // Performance assessment
            if (successRate >= 0.8 && avgSatisfaction >= 0.7) {
                analysis.setFinancialHealthScore("Excellent - Consistently achieving financial goals");
            } else if (successRate >= 0.6 && avgSatisfaction >= 0.6) {
                analysis.setFinancialHealthScore("Good - Generally on track with room for improvement");
            } else if (successRate >= 0.4 && avgSatisfaction >= 0.5) {
                analysis.setFinancialHealthScore("Fair - Some challenges but making progress");
            } else {
                analysis.setFinancialHealthScore("Needs Attention - Consider reviewing financial strategy");
            }
            
            // Goal progress based on topic analysis
            Map<String, Long> topicCounts = episodes.stream()
                .filter(e -> e.getConversationTopic() != null)
                .collect(Collectors.groupingBy(EpisodicMemory::getConversationTopic, Collectors.counting()));
            
            if (!topicCounts.isEmpty()) {
                String primaryFocus = topicCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("general");
                
                analysis.setGoalProgress("Primary focus area: " + primaryFocus + 
                    " (" + topicCounts.get(primaryFocus) + " interactions)");
            }
            
            // Optimization opportunities based on patterns
            if (successRate < 0.7) {
                analysis.addOptimizationOpportunity("Improve decision-making process - success rate below optimal");
            }
            if (avgSatisfaction < 0.6) {
                analysis.addOptimizationOpportunity("Review goal alignment - satisfaction scores indicate potential mismatch");
            }
            
            // Add trend analysis
            LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
            List<EpisodicMemory> recentEpisodes = episodes.stream()
                .filter(e -> e.getTimestamp().isAfter(monthAgo))
                .toList();
            
            if (!recentEpisodes.isEmpty()) {
                double recentSuccessRate = recentEpisodes.stream()
                    .mapToDouble(e -> e.isSuccessful() ? 1.0 : 0.0)
                    .average()
                    .orElse(0.0);
                
                if (recentSuccessRate > successRate + 0.1) {
                    analysis.addOptimizationOpportunity("Positive trend - recent performance improving");
                } else if (recentSuccessRate < successRate - 0.1) {
                    analysis.addOptimizationOpportunity("Declining trend - review recent changes in approach");
                }
            }
        }
        
        // Add current financial analysis if context available
        if (hasRequiredFinancialContext(request) && request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
            double savingsRate = (request.getMonthlyIncome() - request.getMonthlyExpenses()) / request.getMonthlyIncome();
            analysis.setSavingsRate(savingsRate);
            analysis.setBudgetUtilization(request.getMonthlyExpenses() / request.getMonthlyIncome());
        }
        
        response.setFinancialAnalysis(analysis);
    }
}

