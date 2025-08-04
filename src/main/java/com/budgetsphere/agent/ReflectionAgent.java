package com.budgetsphere.agent;

import com.budgetsphere.model.AgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 🪞 Reflection Agent - Reflection Pattern
 * Este agente implementa o padrão de Reflexão:
 * 1. REVIEW: Revisa resultados e decisões passadas
 * 2. REFLECT: Reflete sobre o que funcionou e o que não funcionou
 * 3. LEARN: Aprende com a experiência e ajusta estratégias
 * Usado para: Análise retrospectiva, melhoria contínua, aprendizado de padrões
 */
@Slf4j
@Component
public class ReflectionAgent extends BaseAgent {
    
    public ReflectionAgent() {
        super("ReflectionAgent", "Reflection Pattern");
    }
    
    @Override
    public AgentResponse process(String input, Map<String, Object> context) {
        log.info("🪞 ReflectionAgent iniciando reflexão sobre: {}", input);
        
        try {
            // STEP 1: REVIEW - Revisar dados históricos
            String review = review(input, context);
            log.debug("📖 REVIEW: {}", review);
            
            // STEP 2: REFLECT - Refletir sobre padrões e resultados
            String reflection = reflect(review, context);
            log.debug("🤔 REFLECT: {}", reflection);
            
            // STEP 3: LEARN - Extrair aprendizados e melhorias
            String learning = learn(reflection, context);
            log.debug("🎓 LEARN: {}", learning);
            
            Map<String, Object> data = new HashMap<>();
            data.put("review", review);
            data.put("reflection", reflection);
            data.put("learning", learning);
            data.put("reflectionCycle", "review -> reflect -> learn");
            
            return createResponse(
                "Reflexão concluída com aprendizados: " + learning,
                data,
                true
            );
            
        } catch (Exception e) {
            log.error("❌ Erro no ReflectionAgent: {}", e.getMessage());
            return createResponse("Erro na reflexão: " + e.getMessage(), null, false);
        }
    }
    
    /**
     * REVIEW: Revisa dados e resultados históricos
     */
    private String review(String input, Map<String, Object> context) {
        String prompt = String.format("""
            Como consultor financeiro experiente, REVISE os seguintes dados:
            
            Período/Situação: %s
            Dados históricos: %s
            
            Faça uma revisão estruturada:
            1. RESULTADOS ALCANÇADOS
            2. METAS ATINGIDAS vs NÃO ATINGIDAS
            3. DECISÕES TOMADAS
            4. EVENTOS SIGNIFICATIVOS
            5. PADRÕES OBSERVADOS
            
            Seja factual e objetivo na revisão.
            """, input, context.toString());
            
        return callAI(prompt);
    }
    
    /**
     * REFLECT: Reflete sobre os dados revisados
     */
    private String reflect(String review, Map<String, Object> context) {
        String prompt = String.format("""
            Baseado na revisão: "%s"
            
            REFLITA profundamente sobre:
            
            1. O QUE FUNCIONOU BEM?
               - Quais estratégias foram eficazes?
               - Que comportamentos levaram ao sucesso?
            
            2. O QUE NÃO FUNCIONOU?
               - Onde houve falhas ou desvios?
               - Quais foram os obstáculos?
            
            3. POR QUE aconteceu assim?
               - Quais foram as causas raiz?
               - Que fatores influenciaram os resultados?
            
            4. PADRÕES IDENTIFICADOS
               - Há comportamentos recorrentes?
               - Existem tendências claras?
            """, review);
            
        return callAI(prompt);
    }
    
    /**
     * LEARN: Extrai aprendizados e define melhorias
     */
    private String learn(String reflection, Map<String, Object> context) {
        String prompt = String.format("""
            Baseado na reflexão: "%s"
            
            Extraia APRENDIZADOS PRÁTICOS:
            
            1. LIÇÕES APRENDIDAS
               - Que insights importantes foram descobertos?
               - Que conhecimento pode ser aplicado no futuro?
            
            2. MELHORIAS ESPECÍFICAS
               - Que mudanças devem ser implementadas?
               - Como evitar erros passados?
            
            3. ESTRATÉGIAS FUTURAS
               - Que abordagens devem ser mantidas?
               - Que novas estratégias devem ser testadas?
            
            4. AÇÕES CONCRETAS
               - Que passos específicos devem ser tomados?
               - Como medir o progresso?
            
            Forneça recomendações práticas e acionáveis.
            """, reflection);
            
        return callAI(prompt);
    }
}

