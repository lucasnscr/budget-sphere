package com.budgetsphere.controller;

import com.budgetsphere.model.AgentResponse;
import com.budgetsphere.service.BudgetSphereService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🌐 BudgetSphere REST Controller
 * 
 * Endpoints REST para interação com os agentes de IA financeira
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/budgetsphere")
@CrossOrigin(origins = "*")
public class BudgetSphereController {
    
    @Autowired
    private BudgetSphereService budgetSphereService;
    
    /**
     * Endpoint principal - informações da aplicação
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "BudgetSphere");
        response.put("version", "2.0.0-Simple");
        response.put("description", "Assistente Financeiro com Agentes de IA");
        response.put("patterns", new String[]{
            "ReAct Pattern", "Planning Pattern", "Reflection Pattern", 
            "Multi-Agent Pattern", "Tool Use Pattern"
        });
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("agents", "4 agentes ativos");
        response.put("tools", "2 ferramentas disponíveis");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 👥 Multi-Agent Pattern - Usa o supervisor para orquestrar agentes
     */
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@RequestBody Map<String, Object> request) {
        log.info("🗣️ Chat recebido: {}", request);
        
        String message = (String) request.get("message");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.getOrDefault("context", new HashMap<>());
        
        AgentResponse response = budgetSphereService.processWithSupervisor(message, context);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 🔄 ReAct Pattern - Análise com raciocínio e ação
     */
    @PostMapping("/analyze")
    public ResponseEntity<AgentResponse> analyze(@RequestBody Map<String, Object> request) {
        log.info("🔍 Análise solicitada: {}", request);
        
        String input = (String) request.get("input");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.getOrDefault("context", new HashMap<>());
        
        AgentResponse response = budgetSphereService.processWithReact(input, context);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 📋 Planning Pattern - Criação de planos financeiros
     */
    @PostMapping("/plan")
    public ResponseEntity<AgentResponse> plan(@RequestBody Map<String, Object> request) {
        log.info("📋 Planejamento solicitado: {}", request);
        
        String goal = (String) request.get("goal");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.getOrDefault("context", new HashMap<>());
        
        AgentResponse response = budgetSphereService.processWithPlanning(goal, context);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 🪞 Reflection Pattern - Análise retrospectiva e aprendizado
     */
    @PostMapping("/reflect")
    public ResponseEntity<AgentResponse> reflect(@RequestBody Map<String, Object> request) {
        log.info("🪞 Reflexão solicitada: {}", request);
        
        String period = (String) request.get("period");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.getOrDefault("context", new HashMap<>());
        
        AgentResponse response = budgetSphereService.processWithReflection(period, context);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 🛠️ Tool Use Pattern - Execução de ferramentas específicas
     */
    @PostMapping("/tools/{toolName}")
    public ResponseEntity<Map<String, Object>> executeTool(
            @PathVariable String toolName,
            @RequestBody Map<String, Object> parameters) {
        log.info("🔧 Ferramenta {} solicitada com parâmetros: {}", toolName, parameters);
        
        Map<String, Object> result = budgetSphereService.executeTool(toolName, parameters);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Lista todas as ferramentas disponíveis
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> listTools() {
        Map<String, Object> tools = budgetSphereService.listTools();
        return ResponseEntity.ok(tools);
    }
    
    /**
     * Lista todos os agentes disponíveis
     */
    @GetMapping("/agents")
    public ResponseEntity<Map<String, Object>> listAgents() {
        Map<String, Object> agents = budgetSphereService.listAgents();
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Informações sobre um agente específico
     */
    @GetMapping("/agents/{agentName}")
    public ResponseEntity<Map<String, Object>> getAgentInfo(@PathVariable String agentName) {
        Map<String, Object> info = budgetSphereService.getAgentInfo(agentName);
        return ResponseEntity.ok(info);
    }
    
    /**
     * Exemplo de uso - demonstra todos os patterns
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> demo() {
        Map<String, Object> demo = new HashMap<>();
        
        demo.put("description", "Demonstração dos Patterns de IA do BudgetSphere");
        
        Map<String, Object> examples = new HashMap<>();
        
        examples.put("chat", Map.of(
            "url", "/api/v1/budgetsphere/chat",
            "method", "POST",
            "pattern", "Multi-Agent Pattern",
            "body", Map.of("message", "Preciso de ajuda com meu orçamento mensal")
        ));
        
        examples.put("analyze", Map.of(
            "url", "/api/v1/budgetsphere/analyze",
            "method", "POST", 
            "pattern", "ReAct Pattern",
            "body", Map.of("input", "Gastei R$ 2000 em alimentação este mês")
        ));
        
        examples.put("plan", Map.of(
            "url", "/api/v1/budgetsphere/plan",
            "method", "POST",
            "pattern", "Planning Pattern", 
            "body", Map.of("goal", "Quero economizar R$ 1000 por mês")
        ));
        
        examples.put("reflect", Map.of(
            "url", "/api/v1/budgetsphere/reflect",
            "method", "POST",
            "pattern", "Reflection Pattern",
            "body", Map.of("period", "últimos 3 meses")
        ));
        
        examples.put("tools", Map.of(
            "url", "/api/v1/budgetsphere/tools/BudgetCalculator",
            "method", "POST",
            "pattern", "Tool Use Pattern",
            "body", Map.of("monthlyIncome", "5000")
        ));
        
        demo.put("examples", examples);
        
        return ResponseEntity.ok(demo);
    }
}

