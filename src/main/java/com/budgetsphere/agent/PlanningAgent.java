package com.budgetsphere.agent;

import com.budgetsphere.dto.AgentRequest;
import com.budgetsphere.dto.AgentResponse;
import com.budgetsphere.memory.MemoryOrchestrationService;
import com.budgetsphere.model.MemoryContext;
import com.budgetsphere.model.ProceduralMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Planning Agent - handles complex financial planning and strategic advice
 * Specializes in long-term financial strategies, retirement planning, and comprehensive financial plans
 */
@Component
@Slf4j
public class PlanningAgent extends BaseAgent {

    public PlanningAgent(ChatClient chatClient, MemoryOrchestrationService memoryService) {
        super(chatClient, memoryService);
    }

    @Override
    public String getAgentName() {
        return "PlanningAgent";
    }

    @Override
    public String getAgentDescription() {
        return "Strategic planning agent for complex financial planning, long-term goals, and comprehensive financial strategies";
    }

    @Override
    public boolean canHandle(AgentRequest request) {
        // PlanningAgent is best for complex planning requests
        return request.isComplexPlanningRequest() || 
               hasRequiredFinancialContext(request) ||
               request.getMessage().toLowerCase().contains("plan");
    }

    @Override
    public double getConfidenceScore(AgentRequest request) {
        String message = request.getMessage().toLowerCase();
        
        // High confidence for planning-related requests
        if (request.isComplexPlanningRequest()) {
            return 0.9;
        }
        
        if (message.contains("plan") || 
            message.contains("strategy") || 
            message.contains("long-term") ||
            message.contains("retirement") ||
            message.contains("aposentadoria") ||
            message.contains("estratégia") ||
            message.contains("planejamento")) {
            return 0.8;
        }
        
        // Medium confidence if has financial context for planning
        if (hasRequiredFinancialContext(request)) {
            return 0.6;
        }
        
        // Lower confidence for simple questions
        if (message.contains("calculate") || 
            message.contains("what is") ||
            message.contains("calcular")) {
            return 0.3;
        }
        
        return 0.4; // Default confidence
    }

    @Override
    protected AgentResponse processWithMemory(AgentRequest request, MemoryContext memoryContext) {
        log.info("PlanningAgent processing request: {}", request.getMessage());
        
        try {
            // Execute the chat request with planning-focused prompt
            AgentResponse response = executeChatRequest(request, memoryContext);
            
            // Add planning-specific recommendations
            addPlanningRecommendations(response, request, memoryContext);
            
            // Add strategic next steps
            addStrategicNextSteps(response, request);
            
            // Add comprehensive financial analysis
            if (hasRequiredFinancialContext(request)) {
                addComprehensiveFinancialAnalysis(response, request);
            }
            
            response.setActionTaken("strategic_planning");
            
            return response;
            
        } catch (Exception e) {
            log.error("Error in PlanningAgent processing: {}", e.getMessage(), e);
            return AgentResponse.error(
                request.getSessionId(),
                request.getUserId(),
                getAgentName(),
                "Error creating your financial plan: " + e.getMessage(),
                "PlanningAgent processing error"
            );
        }
    }

    @Override
    protected String buildSystemPrompt(AgentRequest request, MemoryContext memoryContext) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("""
            You are PlanningAgent, a specialized financial planning assistant focused on creating\s
            comprehensive, strategic financial plans and long-term wealth building strategies.
           \s
            Your role:
            - Create detailed, personalized financial plans
            - Develop long-term investment strategies
            - Plan for major life goals (retirement, home purchase, education)
            - Provide strategic advice for wealth building
            - Consider tax implications and optimization strategies
            - Create actionable, step-by-step implementation plans
           \s
            Guidelines:
            - Think strategically and long-term (5+ years)
            - Consider multiple scenarios and contingencies
            - Provide specific, actionable recommendations
            - Include timelines and milestones
            - Address risk management and insurance needs
            - Consider tax-efficient strategies
            - Be thorough but organized in your planning
            - Use Brazilian Portuguese if the user writes in Portuguese
           \s
            Planning Framework:
            1. Assess current financial situation
            2. Clarify goals and priorities
            3. Identify gaps and opportunities
            4. Develop strategic recommendations
            5. Create implementation timeline
            6. Establish monitoring and review process
           \s
           \s""");
        
        // Add comprehensive financial context
        if (hasRequiredFinancialContext(request)) {
            prompt.append("User's Financial Profile:\n");
            if (request.getMonthlyIncome() != null) {
                prompt.append("- Monthly Income: R$ ").append(String.format("%.2f", request.getMonthlyIncome())).append("\n");
                prompt.append("- Annual Income: R$ ").append(String.format("%.2f", request.getMonthlyIncome() * 12)).append("\n");
            }
            if (request.getMonthlyExpenses() != null) {
                prompt.append("- Monthly Expenses: R$ ").append(String.format("%.2f", request.getMonthlyExpenses())).append("\n");
                prompt.append("- Annual Expenses: R$ ").append(String.format("%.2f", request.getMonthlyExpenses() * 12)).append("\n");
            }
            if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
                double savingsRate = (request.getMonthlyIncome() - request.getMonthlyExpenses()) / request.getMonthlyIncome();
                prompt.append("- Current Savings Rate: ").append(String.format("%.1f%%", savingsRate * 100)).append("\n");
                prompt.append("- Monthly Surplus: R$ ").append(String.format("%.2f", request.getMonthlyIncome() - request.getMonthlyExpenses())).append("\n");
            }
            if (request.getRiskTolerance() != null) {
                prompt.append("- Risk Tolerance: ").append(request.getRiskTolerance()).append("\n");
            }
            if (request.getAge() != null) {
                prompt.append("- Age: ").append(request.getAge()).append(" years\n");
                prompt.append("- Years to Retirement (assuming 65): ").append(Math.max(0, 65 - request.getAge())).append(" years\n");
            }
            if (request.getGoal() != null) {
                prompt.append("- Primary Financial Goal: ").append(request.getGoal()).append("\n");
            }
            if (request.getFinancialGoals() != null) {
                prompt.append("- All Financial Goals: ").append(request.getFinancialGoals()).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add memory-based insights for planning
        if (memoryContext != null && memoryContext.hasSufficientContext()) {
            prompt.append("Historical Context and Insights:\n");
            
            // Add successful planning patterns
            if (!memoryContext.getSuccessPatterns().isEmpty()) {
                prompt.append("Successful Patterns from Past:\n");
                memoryContext.getSuccessPatterns().forEach(pattern -> 
                    prompt.append("- ").append(pattern).append("\n"));
            }
            
            // Add relevant financial knowledge
            if (!memoryContext.getSemanticKnowledge().isEmpty()) {
                prompt.append("Relevant Financial Knowledge:\n");
                memoryContext.getSemanticKnowledge().stream()
                    .limit(5)
                    .forEach(knowledge -> {
                        prompt.append("- ").append(knowledge.getConcept()).append(": ")
                              .append(knowledge.getDefinition()).append("\n");
                        if (knowledge.getRules() != null && !knowledge.getRules().isEmpty()) {
                            knowledge.getRules().forEach(rule -> 
                                prompt.append("  Rule: ").append(rule).append("\n"));
                        }
                    });
            }
            
            // Add effective procedures
            if (!memoryContext.getProcedures().isEmpty()) {
                prompt.append("Proven Planning Procedures:\n");
                memoryContext.getProcedures().stream()
                    .filter(ProceduralMemory::isEffective)
                    .limit(3)
                    .forEach(procedure -> 
                        prompt.append("- ").append(procedure.getProcedureName())
                              .append(" (Success Rate: ").append(String.format("%.1f%%", procedure.getSuccessRate() * 100))
                              .append(")\n"));
            }
            
            prompt.append("\n");
        }
        
        prompt.append("""
            Create a comprehensive, strategic financial plan that addresses the user's specific situation and goals.\s
            Include specific recommendations, timelines, and implementation steps. Consider both short-term and\s
            long-term implications of your recommendations.
           \s""");
        
        return prompt.toString();
    }

    /**
     * Adds planning-specific recommendations
     */
    private void addPlanningRecommendations(AgentResponse response, AgentRequest request, MemoryContext memoryContext) {
        // Add strategic recommendations based on financial profile
        if (request.getAge() != null) {
            int age = request.getAge();
            
            if (age < 30) {
                response.addRecommendation("Focus on aggressive growth investments - you have time to recover from market volatility");
                response.addRecommendation("Prioritize building emergency fund and eliminating high-interest debt");
                response.addRecommendation("Consider starting retirement savings immediately to maximize compound growth");
            } else if (age < 50) {
                response.addRecommendation("Balance growth and stability in your investment portfolio");
                response.addRecommendation("Increase retirement savings contributions if behind on goals");
                response.addRecommendation("Consider life and disability insurance to protect your family");
            } else {
                response.addRecommendation("Shift towards more conservative investments as you approach retirement");
                response.addRecommendation("Maximize catch-up contributions to retirement accounts");
                response.addRecommendation("Create a detailed retirement income strategy");
            }
        }
        
        // Risk tolerance based recommendations
        if (request.getRiskTolerance() != null) {
            switch (request.getRiskTolerance().toUpperCase()) {
                case "HIGH":
                    response.addRecommendation("Consider growth stocks, emerging markets, and alternative investments");
                    response.addRecommendation("Maintain higher equity allocation in your portfolio");
                    break;
                case "LOW":
                    response.addRecommendation("Focus on bonds, CDs, and dividend-paying stocks");
                    response.addRecommendation("Prioritize capital preservation over growth");
                    break;
                default:
                    response.addRecommendation("Maintain balanced portfolio with mix of stocks and bonds");
                    response.addRecommendation("Consider target-date funds for automatic rebalancing");
            }
        }
        
        // Income-based recommendations
        if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
            double savingsRate = (request.getMonthlyIncome() - request.getMonthlyExpenses()) / request.getMonthlyIncome();
            
            if (savingsRate < 0.1) {
                response.addRecommendation("PRIORITY: Increase savings rate to at least 10% through expense reduction or income increase");
                response.addRecommendation("Create detailed budget to identify areas for expense reduction");
            } else if (savingsRate > 0.2) {
                response.addRecommendation("Excellent savings rate! Consider tax-advantaged investment accounts");
                response.addRecommendation("Explore opportunities for tax optimization and estate planning");
            }
        }
        
        // Goal-based recommendations
        if (request.getGoal() != null) {
            String goal = request.getGoal().toLowerCase();
            
            if (goal.contains("retirement") || goal.contains("aposentadoria")) {
                response.addRecommendation("Maximize contributions to tax-advantaged retirement accounts");
                response.addRecommendation("Consider Roth conversions if in lower tax bracket now");
            } else if (goal.contains("house") || goal.contains("home") || goal.contains("casa")) {
                response.addRecommendation("Save for 20% down payment to avoid PMI");
                response.addRecommendation("Improve credit score to qualify for better mortgage rates");
            } else if (goal.contains("education") || goal.contains("educação")) {
                response.addRecommendation("Consider 529 education savings plans for tax advantages");
                response.addRecommendation("Research education tax credits and deductions");
            }
        }
        
        // Memory-based recommendations
        if (memoryContext != null) {
            memoryContext.getRecommendations().forEach(response::addRecommendation);
        }
    }

    /**
     * Adds strategic next steps
     */
    private void addStrategicNextSteps(AgentResponse response, AgentRequest request) {
        response.addNextStep("Review and validate all assumptions in the financial plan");
        response.addNextStep("Set up automatic transfers and investments to implement the plan");
        response.addNextStep("Schedule quarterly reviews to monitor progress and make adjustments");
        
        if (hasRequiredFinancialContext(request)) {
            response.addNextStep("Create detailed budget tracking to ensure plan adherence");
            response.addNextStep("Research specific investment products that align with your strategy");
        }
        
        if (request.getAge() != null && request.getAge() > 40) {
            response.addNextStep("Consider consulting with a fee-only financial planner for complex strategies");
            response.addNextStep("Review and update estate planning documents");
        }
        
        response.addNextStep("Set up regular plan reviews (annually or when life circumstances change)");
    }

    /**
     * Adds comprehensive financial analysis for planning
     */
    private void addComprehensiveFinancialAnalysis(AgentResponse response, AgentRequest request) {
        AgentResponse.FinancialAnalysis analysis = AgentResponse.FinancialAnalysis.builder().build();
        
        if (request.getMonthlyIncome() != null && request.getMonthlyExpenses() != null) {
            double income = request.getMonthlyIncome();
            double expenses = request.getMonthlyExpenses();
            double savingsAmount = income - expenses;
            double savingsRate = savingsAmount / income;
            
            analysis.setSavingsRate(savingsRate);
            analysis.setBudgetUtilization(expenses / income);
            
            // Detailed financial health assessment
            if (savingsRate >= 0.25) {
                analysis.setFinancialHealthScore("Excellent - On track for early retirement");
            } else if (savingsRate >= 0.15) {
                analysis.setFinancialHealthScore("Very Good - Strong financial foundation");
            } else if (savingsRate >= 0.10) {
                analysis.setFinancialHealthScore("Good - Meeting minimum savings goals");
            } else if (savingsRate >= 0.05) {
                analysis.setFinancialHealthScore("Fair - Need to increase savings");
            } else {
                analysis.setFinancialHealthScore("Poor - Immediate action required");
            }
            
            // Emergency fund recommendation
            double recommendedEmergencyFund = expenses * 6; // 6 months of expenses
            analysis.setEmergencyFundMonths(6.0);
            
            // Investment recommendations based on planning perspective
            if (savingsRate > 0.15) {
                analysis.setInvestmentRecommendation("Aggressive growth portfolio with 80-90% stocks for long-term wealth building");
            } else if (savingsRate > 0.10) {
                analysis.setInvestmentRecommendation("Balanced growth portfolio with 70-80% stocks");
            } else {
                analysis.setInvestmentRecommendation("Focus on emergency fund first, then conservative growth investments");
            }
            
            // Strategic optimization opportunities
            if (expenses / income > 0.75) {
                analysis.addOptimizationOpportunity("Comprehensive expense review - target 25% expense reduction");
            }
            if (savingsRate < 0.15) {
                analysis.addOptimizationOpportunity("Increase income through career development or side income");
            }
            if (request.getAge() != null && request.getAge() > 35 && savingsRate < 0.20) {
                analysis.addOptimizationOpportunity("Accelerate savings to catch up on retirement goals");
            }
            
            // Goal progress assessment
            if (request.getGoal() != null) {
                String goal = request.getGoal().toLowerCase();
                if (goal.contains("retirement")) {
                    // Simple retirement calculation
                    int yearsToRetirement = request.getAge() != null ? Math.max(0, 65 - request.getAge()) : 30;
                    double annualSavings = savingsAmount * 12;
                    // Assuming 7% annual return
                    double futureValue = annualSavings * (Math.pow(1.07, yearsToRetirement) - 1) / 0.07;
                    analysis.setGoalProgress(String.format("Projected retirement savings: R$ %.0f in %d years", 
                        futureValue, yearsToRetirement));
                }
            }
        }
        
        response.setFinancialAnalysis(analysis);
    }
}

