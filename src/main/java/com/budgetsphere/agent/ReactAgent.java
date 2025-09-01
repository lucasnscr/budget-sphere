package com.budgetsphere.agent;

import com.budgetsphere.dto.AgentRequest;
import com.budgetsphere.dto.AgentResponse;
import com.budgetsphere.memory.MemoryOrchestrationService;
import com.budgetsphere.model.MemoryContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * React Agent - handles immediate questions and quick financial calculations
 * Specializes in reactive responses to user queries with fast, accurate answers
 */
@Component
@Slf4j
public class ReactAgent extends BaseAgent {

    public ReactAgent(ChatClient chatClient, MemoryOrchestrationService memoryService) {
        super(chatClient, memoryService);
    }

    @Override
    public String getAgentName() {
        return "ReactAgent";
    }

    @Override
    public String getAgentDescription() {
        return "Reactive agent for immediate financial questions, calculations, and quick advice";
    }

    @Override
    public boolean canHandle(AgentRequest request) {
        // ReactAgent can handle most general requests
        return true;
    }

    @Override
    public double getConfidenceScore(AgentRequest request) {
        String message = request.getMessage().toLowerCase();
        
        // High confidence for calculation and immediate questions
        if (message.contains("calculate") || 
            message.contains("how much") || 
            message.contains("what is") ||
            message.contains("quanto") ||
            message.contains("calcular")) {
            return 0.9;
        }
        
        // Medium confidence for general questions
        if (message.contains("?") || 
            message.contains("help") || 
            message.contains("ajuda") ||
            message.contains("explain") ||
            message.contains("explique")) {
            return 0.7;
        }
        
        // Lower confidence for complex planning
        if (request.isComplexPlanningRequest() || request.isReflectionRequest()) {
            return 0.3;
        }
        
        return 0.6; // Default confidence for general requests
    }

    @Override
    protected AgentResponse processWithMemory(AgentRequest request, MemoryContext memoryContext) {
        log.info("ReactAgent processing request: {}", request.getMessage());
        
        try {
            // Execute the chat request
            AgentResponse response = executeChatRequest(request, memoryContext);
            
            // Add specific recommendations based on request type
            addReactiveRecommendations(response, request, memoryContext);
            
            // Add next steps
            addNextSteps(response, request);
            
            // Add financial analysis if applicable
            if (hasRequiredFinancialContext(request)) {
                addFinancialAnalysis(response, request);
            }
            
            response.setActionTaken("reactive_response");
            
            return response;
            
        } catch (Exception e) {
            log.error("Error in ReactAgent processing: {}", e.getMessage(), e);
            return AgentResponse.error(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                "Error processing your request: " + e.getMessage(),
                "ReactAgent processing error"
            );
        }
    }

    @Override
    protected String buildSystemPrompt(AgentRequest request, MemoryContext memoryContext) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("""
            You are ReactAgent, a specialized financial assistant focused on providing immediate,\s
            accurate responses to financial questions and calculations.
           \s
            Your role:
            - Provide quick, accurate answers to financial questions
            - Perform financial calculations and explain the results
            - Give immediate practical advice based on user's financial situation
            - Be concise but thorough in your explanations
            - Use the user's financial context to personalize responses
           \s
            Guidelines:
            - Always be accurate with calculations
            - Explain your reasoning clearly
            - Provide actionable advice
            - Reference relevant financial principles
            - Be encouraging and supportive
            - Use Brazilian Portuguese if the user writes in Portuguese
           \s
           \s""");
        
        // Add financial context if available
        if (hasRequiredFinancialContext(request)) {
            prompt.append("User's Financial Context:\n");
            if (request.getMonthlyIncome() != null) {
                prompt.append("- Monthly Income: R$ ").append(String.format("%.2f", request.getMonthlyIncome())).append("\n");
            }
            if (request.getMonthlyExpenses() != null) {
                prompt.append("- Monthly Expenses: R$ ").append(String.format("%.2f", request.getMonthlyExpenses())).append("\n");
            }
            if (request.getRiskTolerance() != null) {
                prompt.append("- Risk Tolerance: ").append(request.getRiskTolerance()).append("\n");
            }
            if (request.getAge() != null) {
                prompt.append("- Age: ").append(request.getAge()).append(" years\n");
            }
            if (request.getGoal() != null) {
                prompt.append("- Financial Goal: ").append(request.getGoal()).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add memory insights if available
        if (memoryContext != null && memoryContext.hasSufficientContext()) {
            prompt.append("Relevant Past Experiences:\n");
            
            // Add successful patterns
            if (!memoryContext.getSuccessPatterns().isEmpty()) {
                prompt.append("Success Patterns:\n");
                memoryContext.getSuccessPatterns().forEach(pattern -> 
                    prompt.append("- ").append(pattern).append("\n"));
            }
            
            // Add relevant knowledge
            if (!memoryContext.getSemanticKnowledge().isEmpty()) {
                prompt.append("Relevant Knowledge:\n");
                memoryContext.getSemanticKnowledge().stream()
                    .limit(3)
                    .forEach(knowledge -> 
                        prompt.append("- ").append(knowledge.getConcept()).append(": ")
                              .append(knowledge.getDefinition()).append("\n"));
            }
            
            prompt.append("\n");
        }
        
        prompt.append("""
            Respond in a helpful, professional manner. If calculations are involved,\s
            show your work step by step. Always provide practical, actionable advice.
           \s""");
        
        return prompt.toString();
    }

    /**
     * Adds reactive-specific recommendations
     */
    private void addReactiveRecommendations(AgentResponse response, AgentRequest request, MemoryContext memoryContext) {
        // Add common financial recommendations
        List<String> commonRecs = getCommonRecommendations(request);
        commonRecs.forEach(response::addRecommendation);
        
        // Add memory-based recommendations
        if (memoryContext != null) {
            memoryContext.getRecommendations().forEach(response::addRecommendation);
        }
        
        // Add specific recommendations based on message content
        String message = request.getMessage().toLowerCase();
        
        if (message.contains("emergency") || message.contains("emergência")) {
            response.addRecommendation("Build an emergency fund covering 3-6 months of expenses");
        }
        
        if (message.contains("debt") || message.contains("dívida")) {
            response.addRecommendation("Consider the debt avalanche method: pay minimums on all debts, then focus extra payments on highest interest debt");
        }
        
        if (message.contains("invest") || message.contains("investir")) {
            response.addRecommendation("Start with low-cost index funds for diversified exposure to the market");
        }
        
        if (message.contains("budget") || message.contains("orçamento")) {
            response.addRecommendation("Use the 50/30/20 rule: 50% needs, 30% wants, 20% savings and debt repayment");
        }
    }

    /**
     * Adds next steps for the user
     */
    private void addNextSteps(AgentResponse response, AgentRequest request) {
        String message = request.getMessage().toLowerCase();
        
        if (message.contains("calculate") || message.contains("calcular")) {
            response.addNextStep("Review the calculation results and consider how they fit into your overall financial plan");
            response.addNextStep("If you need help creating a comprehensive plan, ask about long-term financial planning");
        }
        
        if (message.contains("budget") || message.contains("orçamento")) {
            response.addNextStep("Track your expenses for a month to validate your budget assumptions");
            response.addNextStep("Set up automatic transfers to ensure you stick to your savings goals");
        }
        
        if (message.contains("invest") || message.contains("investir")) {
            response.addNextStep("Research specific investment options that match your risk tolerance");
            response.addNextStep("Consider consulting with a financial advisor for personalized investment advice");
        }
        
        // Always add a general next step
        response.addNextStep("Feel free to ask follow-up questions or request more detailed analysis");
    }

    /**
     * Adds financial analysis to the response
     */
    private void addFinancialAnalysis(AgentResponse response, AgentRequest request) {
        AgentResponse.FinancialAnalysis analysis = AgentResponse.FinancialAnalysis.builder().build();
        
        // Calculate basic metrics
        if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
            double income = request.getMonthlyIncome();
            double expenses = request.getMonthlyExpenses();
            double savingsAmount = income - expenses;
            double savingsRate = savingsAmount / income;
            
            analysis.setSavingsRate(savingsRate);
            analysis.setBudgetUtilization(expenses / income);
            
            // Financial health assessment
            if (savingsRate >= 0.2) {
                analysis.setFinancialHealthScore("Excellent");
            } else if (savingsRate >= 0.1) {
                analysis.setFinancialHealthScore("Good");
            } else if (savingsRate >= 0.05) {
                analysis.setFinancialHealthScore("Fair");
            } else {
                analysis.setFinancialHealthScore("Needs Improvement");
            }
            
            // Emergency fund calculation
            double emergencyFundMonths = 0.0; // Would need current savings data
            analysis.setEmergencyFundMonths(emergencyFundMonths);
            
            // Risk assessment based on savings rate and age
            if (request.getAge() != null) {
                if (request.getAge() < 30 && savingsRate > 0.15) {
                    analysis.setRiskAssessment("Can afford moderate to high risk investments");
                } else if (request.getAge() > 50 && savingsRate < 0.1) {
                    analysis.setRiskAssessment("Should focus on conservative investments and catch-up savings");
                } else {
                    analysis.setRiskAssessment("Balanced approach recommended");
                }
            }
            
            // Investment recommendations
            if (savingsRate > 0.1) {
                analysis.setInvestmentRecommendation("Consider investing excess savings in diversified portfolio");
            } else {
                analysis.setInvestmentRecommendation("Focus on building emergency fund before investing");
            }
            
            // Optimization opportunities
            if (expenses / income > 0.8) {
                analysis.addOptimizationOpportunity("Review and reduce monthly expenses");
            }
            if (savingsRate < 0.1) {
                analysis.addOptimizationOpportunity("Increase savings rate to at least 10%");
            }
        }
        
        response.setFinancialAnalysis(analysis);
    }
}

