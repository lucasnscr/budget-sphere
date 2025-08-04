package com.budgetsphere.agent;

import com.budgetsphere.model.AgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 👥 Supervisor Agent - Multi-Agent Pattern
 * Este agente implementa o padrão Multi-Agent:
 * 1. ROUTE: Decide qual agente é mais adequado para a tarefa
 * 2. COORDINATE: Coordena a execução entre múltiplos agentes
 * 3. SYNTHESIZE: Combina resultados de diferentes agentes
 * Usado para: Orquestração de agentes, decisões complexas, workflows multi-etapa
 */
@Slf4j
@Component
public class SupervisorAgent extends BaseAgent {
    
    @Autowired
    private ReactAgent reactAgent;
    
    @Autowired
    private PlanningAgent planningAgent;
    
    @Autowired
    private ReflectionAgent reflectionAgent;
    
    public SupervisorAgent() {
        super("SupervisorAgent", "Multi-Agent Pattern");
    }
    
    @Override
    public AgentResponse process(String input, Map<String, Object> context) {
        log.info("👥 SupervisorAgent orquestrando para: {}", input);
        
        try {
            // STEP 1: ROUTE - Decidir qual agente usar
            String routing = route(input, context);
            log.debug("🎯 ROUTE: {}", routing);
            
            // STEP 2: COORDINATE - Executar agentes apropriados
            List<AgentResponse> responses = coordinate(routing, input, context);
            log.debug("🔄 COORDINATE: {} agentes executados", responses.size());
            
            // STEP 3: SYNTHESIZE - Combinar resultados
            String synthesis = synthesize(responses, context);
            log.debug("🔗 SYNTHESIZE: {}", synthesis);
            
            Map<String, Object> data = new HashMap<>();
            data.put("routing", routing);
            data.put("agentResponses", responses);
            data.put("synthesis", synthesis);
            data.put("multiAgentFlow", "route -> coordinate -> synthesize");
            
            return createResponse(
                "Orquestração multi-agente concluída: " + synthesis,
                data,
                true
            );
            
        } catch (Exception e) {
            log.error("❌ Erro no SupervisorAgent: {}", e.getMessage());
            return createResponse("Erro na orquestração: " + e.getMessage(), null, false);
        }
    }
    
    /**
     * ROUTE: Decide quais agentes usar baseado na entrada
     */
    private String route(String input, Map<String, Object> context) {
        String prompt = String.format("""
            Como supervisor de agentes de IA financeira, DECIDA qual estratégia usar:
            
            Solicitação: %s
            Contexto: %s
            
            Agentes disponíveis:
            1. ReactAgent - Para análise e ações imediatas (ReAct Pattern)
            2. PlanningAgent - Para criação de planos (Planning Pattern)
            3. ReflectionAgent - Para análise retrospectiva (Reflection Pattern)
            
            Estratégias possíveis:
            - SINGLE: Usar apenas um agente
            - SEQUENTIAL: Usar agentes em sequência
            - PARALLEL: Usar múltiplos agentes em paralelo
            
            Responda no formato:
            ESTRATÉGIA: [SINGLE/SEQUENTIAL/PARALLEL]
            AGENTES: [lista dos agentes]
            JUSTIFICATIVA: [por que esta escolha]
            """, input, context.toString());
            
        return callAI(prompt);
    }
    
    /**
     * COORDINATE: Executa os agentes conforme a estratégia
     */
    private List<AgentResponse> coordinate(String routing, String input, Map<String, Object> context) {
        List<AgentResponse> responses = new ArrayList<>();
        
        // Parse da estratégia (simplificado)
        if (routing.contains("ReactAgent")) {
            log.info("🔄 Executando ReactAgent");
            responses.add(reactAgent.process(input, context));
        }
        
        if (routing.contains("PlanningAgent")) {
            log.info("📋 Executando PlanningAgent");
            responses.add(planningAgent.process(input, context));
        }
        
        if (routing.contains("ReflectionAgent")) {
            log.info("🪞 Executando ReflectionAgent");
            responses.add(reflectionAgent.process(input, context));
        }
        
        // Se nenhum agente foi identificado, usar ReactAgent como padrão
        if (responses.isEmpty()) {
            log.info("🔄 Usando ReactAgent como padrão");
            responses.add(reactAgent.process(input, context));
        }
        
        return responses;
    }
    
    /**
     * SYNTHESIZE: Combina resultados de múltiplos agentes
     */
    private String synthesize(List<AgentResponse> responses, Map<String, Object> context) {
        StringBuilder agentResults = new StringBuilder();
        
        for (AgentResponse response : responses) {
            agentResults.append(String.format("""
                
                === %s (%s) ===
                Resultado: %s
                Sucesso: %s
                """, response.getAgentName(), response.getPattern(), 
                response.getMessage(), response.isSuccess()));
        }
        
        String prompt = String.format("""
            Como supervisor, SINTETIZE os resultados dos agentes:
            
            %s
            
            Crie uma resposta unificada que:
            1. COMBINE os insights de todos os agentes
            2. RESOLVA conflitos ou contradições
            3. PRIORIZE as recomendações mais importantes
            4. FORNEÇA uma conclusão clara e acionável
            
            A resposta deve ser coerente e útil para o usuário.
            """, agentResults.toString());
            
        return callAI(prompt);
    }
}

