package com.budgetsphere.agent;

import com.budgetsphere.model.AgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔄 ReAct Agent - Reasoning and Acting Pattern
 * Este agente implementa o padrão ReAct (Reason + Act):
 * 1. OBSERVE: Analisa a situação financeira atual
 * 2. REASON: Raciocina sobre os dados e identifica padrões
 * 3. ACT: Toma ações baseadas no raciocínio
 * Usado para: Análise de despesas, detecção de anomalias, alertas proativos
 */
@Slf4j
@Component
public class ReactAgent extends BaseAgent {
    
    public ReactAgent() {
        super("ReactAgent", "ReAct Pattern");
    }
    
    @Override
    public AgentResponse process(String input, Map<String, Object> context) {
        log.info("🔄 ReactAgent iniciando ciclo ReAct para: {}", input);
        
        try {
            // STEP 1: OBSERVE - Observar a situação
            String observation = observe(input, context);
            log.debug("👁️ OBSERVE: {}", observation);
            
            // STEP 2: REASON - Raciocinar sobre os dados
            String reasoning = reason(observation, context);
            log.debug("🧠 REASON: {}", reasoning);
            
            // STEP 3: ACT - Decidir ação
            String action = act(reasoning, context);
            log.debug("⚡ ACT: {}", action);
            
            Map<String, Object> data = new HashMap<>();
            data.put("observation", observation);
            data.put("reasoning", reasoning);
            data.put("action", action);
            data.put("cycle", "observe -> reason -> act");
            
            return createResponseWithReasoning(
                "Análise ReAct concluída: " + action,
                data,
                reasoning,
                action,
                true);
        } catch (Exception e) {
            log.error("❌ Erro no ReactAgent: {}", e.getMessage());
            return createResponse("Erro na análise ReAct: " + e.getMessage(), null, false);
        }
    }
    
    /**
     * OBSERVE: Analisa os dados financeiros atuais
     */
    private String observe(String input, Map<String, Object> context) {
        String prompt = String.format("""
            Como analista financeiro, OBSERVE a seguinte situação:
            
            Entrada: %s
            Contexto: %s
            
            Descreva objetivamente o que você observa nos dados financeiros.
            Foque em fatos, números e padrões visíveis.
            """, input, context.toString());
            
        return callAI(prompt);
    }
    
    /**
     * REASON: Raciocina sobre as observações
     */
    private String reason(String observation, Map<String, Object> context) {
        String prompt = String.format("""
            Baseado na observação: "%s"
            
            RACIOCINE sobre os dados:
            1. Que padrões você identifica?
            2. Há algo preocupante ou positivo?
            3. Quais são as possíveis causas?
            4. Que tendências você prevê?
            
            Forneça um raciocínio lógico e estruturado.
            """, observation);
            
        return callAI(prompt);
    }
    
    /**
     * ACT: Decide que ação tomar baseado no raciocínio
     */
    private String act(String reasoning, Map<String, Object> context) {
        String prompt = String.format("""
            Baseado no raciocínio: "%s"
            
            Que AÇÃO específica você recomenda?
            
            Escolha UMA ação principal:
            - Criar alerta
            - Ajustar orçamento
            - Investigar categoria específica
            - Parabenizar pelo progresso
            - Sugerir economia
            - Recomendar investimento
            
            Justifique sua escolha e seja específico.
            """, reasoning);
            
        return callAI(prompt);
    }
}

