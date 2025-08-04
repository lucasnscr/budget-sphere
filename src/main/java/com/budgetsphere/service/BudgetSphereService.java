package com.budgetsphere.service;

import com.budgetsphere.agent.*;
import com.budgetsphere.model.AgentResponse;
import com.budgetsphere.tool.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 🎯 BudgetSphere Service - Orquestração Principal
 * Serviço principal que coordena todos os agentes e ferramentas
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
    private ToolRegistry toolRegistry;
    
    /**
     * Processa uma solicitação usando o supervisor (Multi-Agent Pattern)
     */
    public AgentResponse processWithSupervisor(String input, Map<String, Object> context) {
        log.info("🎯 Processando com SupervisorAgent: {}", input);
        return supervisorAgent.process(input, context != null ? context : new HashMap<>());
    }
    
    /**
     * Processa uma solicitação usando ReAct Pattern
     */
    public AgentResponse processWithReact(String input, Map<String, Object> context) {
        log.info("🔄 Processando com ReactAgent: {}", input);
        return reactAgent.process(input, context != null ? context : new HashMap<>());
    }
    
    /**
     * Processa uma solicitação usando Planning Pattern
     */
    public AgentResponse processWithPlanning(String input, Map<String, Object> context) {
        log.info("📋 Processando com PlanningAgent: {}", input);
        return planningAgent.process(input, context != null ? context : new HashMap<>());
    }
    
    /**
     * Processa uma solicitação usando Reflection Pattern
     */
    public AgentResponse processWithReflection(String input, Map<String, Object> context) {
        log.info("🪞 Processando com ReflectionAgent: {}", input);
        return reflectionAgent.process(input, context != null ? context : new HashMap<>());
    }
    
    /**
     * Executa uma ferramenta específica (Tool Use Pattern)
     */
    public Map<String, Object> executeTool(String toolName, Map<String, Object> parameters) {
        log.info("🛠️ Executando ferramenta: {}", toolName);
        return toolRegistry.executeTool(toolName, parameters != null ? parameters : new HashMap<>());
    }
    
    /**
     * Lista todas as ferramentas disponíveis
     */
    public Map<String, Object> listTools() {
        Map<String, Object> response = new HashMap<>();
        response.put("tools", toolRegistry.listTools());
        response.put("count", toolRegistry.listTools().size());
        return response;
    }
    
    /**
     * Obtém informações sobre um agente específico
     */
    public Map<String, Object> getAgentInfo(String agentName) {
        Map<String, Object> info = new HashMap<>();
        
        switch (agentName.toLowerCase()) {
            case "supervisor":
                info.put("name", supervisorAgent.getAgentName());
                info.put("pattern", supervisorAgent.getPattern());
                info.put("description", "Orquestra múltiplos agentes para resolver problemas complexos");
                break;
            case "react":
                info.put("name", reactAgent.getAgentName());
                info.put("pattern", reactAgent.getPattern());
                info.put("description", "Analisa, raciocina e age baseado em observações");
                break;
            case "planning":
                info.put("name", planningAgent.getAgentName());
                info.put("pattern", planningAgent.getPattern());
                info.put("description", "Cria planos financeiros estruturados e otimizados");
                break;
            case "reflection":
                info.put("name", reflectionAgent.getAgentName());
                info.put("pattern", reflectionAgent.getPattern());
                info.put("description", "Reflete sobre resultados passados e aprende com experiências");
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
        
        return response;
    }
}

