package com.budgetsphere.agent;

import com.budgetsphere.model.AgentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 📋 Planning Agent - Planning Pattern
 * Este agente implementa o padrão de Planejamento:
 * 1. ANALYZE: Analisa a situação financeira atual
 * 2. PLAN: Cria um plano detalhado e estruturado
 * 3. OPTIMIZE: Otimiza o plano baseado em regras financeiras
 * Usado para: Criação de orçamentos, planejamento de metas, estratégias de economia
 */
@Slf4j
@Component
public class PlanningAgent extends BaseAgent {
    
    public PlanningAgent() {
        super("PlanningAgent", "Planning Pattern");
    }
    
    @Override
    public AgentResponse process(String input, Map<String, Object> context) {
        log.info("📋 PlanningAgent iniciando planejamento para: {}", input);
        
        try {
            // STEP 1: ANALYZE - Analisar situação atual
            String analysis = analyze(input, context);
            log.debug("📊 ANALYZE: {}", analysis);
            
            // STEP 2: PLAN - Criar plano estruturado
            String plan = createPlan(analysis, context);
            log.debug("📝 PLAN: {}", plan);
            
            // STEP 3: OPTIMIZE - Otimizar o plano
            String optimizedPlan = optimize(plan, context);
            log.debug("⚡ OPTIMIZE: {}", optimizedPlan);
            
            Map<String, Object> data = new HashMap<>();
            data.put("analysis", analysis);
            data.put("initialPlan", plan);
            data.put("optimizedPlan", optimizedPlan);
            data.put("planningSteps", "analyze -> plan -> optimize");
            
            return createResponse(
                "Plano financeiro criado e otimizado: " + optimizedPlan,
                data,
                true
            );
            
        } catch (Exception e) {
            log.error("❌ Erro no PlanningAgent: {}", e.getMessage());
            return createResponse("Erro no planejamento: " + e.getMessage(), null, false);
        }
    }
    
    /**
     * ANALYZE: Analisa a situação financeira atual
     */
    private String analyze(String input, Map<String, Object> context) {
        String prompt = String.format("""
            Como planejador financeiro, ANALISE a seguinte situação:
            
            Solicitação: %s
            Dados disponíveis: %s
            
            Forneça uma análise estruturada:
            1. Situação atual
            2. Recursos disponíveis
            3. Limitações identificadas
            4. Oportunidades
            5. Riscos
            
            Seja objetivo e quantitativo quando possível.
            """, input, context.toString());
            
        return callAI(prompt);
    }
    
    /**
     * PLAN: Cria um plano detalhado
     */
    private String createPlan(String analysis, Map<String, Object> context) {
        String prompt = String.format("""
            Baseado na análise: "%s"
            
            Crie um PLANO DETALHADO com:
            
            1. OBJETIVO PRINCIPAL
            2. METAS ESPECÍFICAS (com valores e prazos)
            3. ESTRATÉGIAS (como alcançar cada meta)
            4. RECURSOS NECESSÁRIOS
            5. CRONOGRAMA (passos mensais)
            6. INDICADORES DE SUCESSO
            
            Use a regra 50/30/20 como base quando aplicável:
            - 50%% necessidades
            - 30%% desejos  
            - 20%% poupança/investimentos
            """, analysis);
            
        return callAI(prompt);
    }
    
    /**
     * OPTIMIZE: Otimiza o plano baseado em melhores práticas
     */
    private String optimize(String plan, Map<String, Object> context) {
        String prompt = String.format("""
            Plano inicial: "%s"
            
            OTIMIZE este plano considerando:
            
            1. EFICIÊNCIA: Como tornar mais eficiente?
            2. REALISMO: O plano é realista e alcançável?
            3. FLEXIBILIDADE: Como adaptar a mudanças?
            4. PRIORIZAÇÃO: Qual a ordem de prioridade?
            5. CONTINGÊNCIA: E se algo der errado?
            
            Forneça o plano otimizado com justificativas das mudanças.
            """, plan);
            
        return callAI(prompt);
    }
}

