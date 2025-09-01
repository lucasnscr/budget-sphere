package com.budgetsphere.service;

import com.budgetsphere.agent.*;
import com.budgetsphere.dto.AgentRequest;
import com.budgetsphere.dto.AgentResponse;
import com.budgetsphere.memory.MemoryOrchestrationService;
import com.budgetsphere.model.MemoryContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🎯 BudgetSphere Service - Orquestração Principal
 * Serviço principal que coordena todos os agentes e ferramentas do sistema
 */
@Slf4j
@Service
public class BudgetSphereService {
    
    @Autowired
    private SupervisorAgent supervisorAgent;
    
    @Autowired
    private ReactAgent reactAgent;
    
    @Autowired
    private PlanningAgent planningAgent;
    
    @Autowired
    private ReflectionAgent reflectionAgent;
    
    @Autowired
    private MemoryOrchestrationService memoryService;

    /**
     * Processa uma solicitação usando o supervisor (Multi-Agent Pattern)
     * Este é o ponto de entrada principal para todas as requisições
     */
    public AgentResponse processWithSupervisor(AgentRequest request) {
        log.info("🎯 Processando com SupervisorAgent - User: {}, Session: {}", 
                request.getUserId(), request.getSessionId());
        
        try {

            // Preparar contexto com memória se solicitado
            MemoryContext memoryContext = null;
            if (request.getUseMemory()) {
                memoryContext = memoryService.buildMemoryContext(
                        request.getSessionId(),
                        request.getUserId(),
                        request.getMessage(),
                        request.getContext(),
                        supervisorAgent.getAgentName()
                );
            }

            // Processar com o supervisor
            AgentResponse response = supervisorAgent.processRequest(request);
            
            // Armazenar experiência se solicitado
            if (request.getStoreExperience()) {
                memoryService.storeEpisode(
                    request.getUserId(),
                    request.getSessionId(),
                    response.getAgentUsed(),
                    request.getMessage(),
                    response.getMessage(),
                    buildContextData(request),
                    response.getSuccess(),
                    null, // satisfactionScore será atualizado via feedback
                    null, // userFeedback será atualizado via feedback
                    response.getReasoning(),
                    response.getActionTaken()
                );
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ Erro ao processar com SupervisorAgent", e);
            return AgentResponse.builder()
                    .sessionId(request.getSessionId())
                    .userId(request.getUserId())
                    .agentUsed("SupervisorAgent")
                    .message("Desculpe, ocorreu um erro interno. Tente novamente em alguns instantes.")
                    .success(false)
                    .reasoning("Erro interno do sistema: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
    
    /**
     * Processa uma solicitação usando ReAct Pattern diretamente
     */
    public AgentResponse processWithReact(AgentRequest request) {
        log.info("🔄 Processando com ReactAgent - User: {}", request.getUserId());
        
        MemoryContext memoryContext = null;
        if (request.getUseMemory()) {
            memoryContext = memoryService.buildMemoryContext(
                    request.getSessionId(),
                    request.getUserId(),
                    request.getMessage(),
                    request.getContext(),
                    reactAgent.getAgentName()
            );
        }
        
        return reactAgent.processRequest(request);
    }
    
    /**
     * Processa uma solicitação usando Planning Pattern diretamente
     */
    public AgentResponse processWithPlanning(AgentRequest request) {
        log.info("📋 Processando com PlanningAgent - User: {}", request.getUserId());
        
        MemoryContext memoryContext = null;
        if (request.getUseMemory()) {
            memoryContext = memoryService.buildMemoryContext(
                    request.getSessionId(),
                    request.getUserId(),
                    request.getMessage(),
                    request.getContext(),
                    planningAgent.getAgentName()
            );
        }
        
        return planningAgent.processRequest(request);
    }
    
    /**
     * Processa uma solicitação usando Reflection Pattern diretamente
     */
    public AgentResponse processWithReflection(AgentRequest request) {
        log.info("🪞 Processando com ReflectionAgent - User: {}", request.getUserId());
        
        MemoryContext memoryContext = null;
        if (request.getUseMemory()) {
            memoryContext = memoryService.buildMemoryContext(
                    request.getSessionId(),
                    request.getUserId(),
                    request.getMessage(),
                    request.getContext(),
                    reflectionAgent.getAgentName()
            );
        }
        
        return reflectionAgent.processRequest(request);
    }

    /**
     * Processa feedback do usuário sobre uma resposta
     */
    public void processFeedback(String sessionId, Double satisfactionScore, String feedback) {
        log.info("📝 Processando feedback - Session: {}, Score: {}", sessionId, satisfactionScore);
        try {
            memoryService.updateLatestEpisodeFeedback(sessionId, satisfactionScore, feedback);
            log.info("✅ Feedback processado com sucesso");
        } catch (Exception e) {
            log.error("❌ Erro ao processar feedback", e);
        }
    }
    
    /**
     * Obtém informações sobre um agente específico
     */
    public Map<String, Object> getAgentInfo(String agentName) {
        Map<String, Object> info = new HashMap<>();
        
        switch (agentName.toLowerCase()) {
            case "supervisor":
                info.put("name", "SupervisorAgent");
                info.put("pattern", "Multi-Agent Orchestration");
                info.put("description", "Orquestra múltiplos agentes para resolver problemas complexos");
                info.put("specialties", new String[]{"Roteamento inteligente", "Coordenação de agentes", "Otimização de respostas"});
                break;
            case "react":
                info.put("name", "ReactAgent");
                info.put("pattern", "ReAct (Reasoning + Acting)");
                info.put("description", "Analisa, raciocina e age baseado em observações");
                info.put("specialties", new String[]{"Respostas rápidas", "Cálculos financeiros", "Conselhos diretos"});
                break;
            case "planning":
                info.put("name", "PlanningAgent");
                info.put("pattern", "Strategic Planning");
                info.put("description", "Cria planos financeiros estruturados e otimizados");
                info.put("specialties", new String[]{"Planejamento de aposentadoria", "Estratégias de investimento", "Objetivos de longo prazo"});
                break;
            case "reflection":
                info.put("name", "ReflectionAgent");
                info.put("pattern", "Reflection and Analysis");
                info.put("description", "Reflete sobre resultados passados e aprende com experiências");
                info.put("specialties", new String[]{"Análise de performance", "Insights comportamentais", "Otimização de estratégias"});
                break;
            default:
                info.put("error", "Agente não encontrado: " + agentName);
        }
        
        return info;
    }
    
    /**
     * Lista todos os agentes disponíveis
     */
    public Map<String, Object> listAgents() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> agents = new HashMap<>();
        
        agents.put("supervisor", getAgentInfo("supervisor"));
        agents.put("react", getAgentInfo("react"));
        agents.put("planning", getAgentInfo("planning"));
        agents.put("reflection", getAgentInfo("reflection"));
        
        response.put("agents", agents);
        response.put("count", 4);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    /**
     * Verifica a saúde do sistema
     */
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verificar agentes
            Map<String, Boolean> agentHealth = new HashMap<>();
            agentHealth.put("supervisor", supervisorAgent != null);
            agentHealth.put("react", reactAgent != null);
            agentHealth.put("planning", planningAgent != null);
            agentHealth.put("reflection", reflectionAgent != null);
            
            // Verificar m emória
            boolean memoryHealth = memoryService != null;
            
            // Status geral
            boolean overallHealth = agentHealth.values().stream().allMatch(Boolean::booleanValue) && memoryHealth;
            
            health.put("status", overallHealth ? "UP" : "DOWN");
            health.put("agents", agentHealth);
            health.put("memory", memoryHealth);
            health.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("❌ Erro ao verificar saúde do sistema", e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
        }
        
        return health;
    }

    /**
     * Constrói dados de contexto a partir da requisição
     */
    private Map<String, Object> buildContextData(AgentRequest request) {
        Map<String, Object> contextData = new HashMap<>();
        
        if (request.getMonthlyIncome() != null) {
            contextData.put("monthlyIncome", request.getMonthlyIncome());
        }
        if (request.getMonthlyExpenses() != null) {
            contextData.put("monthlyExpenses", request.getMonthlyExpenses());
        }
        if (request.getAge() != null) {
            contextData.put("age", request.getAge());
        }
        if (request.getRiskTolerance() != null) {
            contextData.put("riskTolerance", request.getRiskTolerance());
        }
        if (request.getGoal() != null) {
            contextData.put("goal", request.getGoal());
        }
        if (request.getFinancialGoals() != null) {
            contextData.put("financialGoals", request.getFinancialGoals());
        }
        if (request.getContext() != null) {
            contextData.putAll(request.getContext());
        }
        
        return contextData;
    }
}

