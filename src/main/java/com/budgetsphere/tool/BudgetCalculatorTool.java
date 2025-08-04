package com.budgetsphere.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 🧮 Budget Calculator Tool - Tool Use Patter
 * Ferramenta para cálculos de orçamento baseados na regra 50/30/20:
 * - 50% para necessidades (moradia, alimentação, transporte)
 * - 30% para desejos (lazer, entretenimento, compras)
 * - 20% para poupança e investimentos
 */
@Slf4j
@Component
public class BudgetCalculatorTool implements FinancialTool {
    
    @Override
    public String getName() {
        return "BudgetCalculator";
    }
    
    @Override
    public String getDescription() {
        return "Calcula distribuição de orçamento baseado na regra 50/30/20";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        log.info("🧮 Executando BudgetCalculator com parâmetros: {}", parameters);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            BigDecimal income = new BigDecimal(parameters.get("monthlyIncome").toString());
            
            // Aplicar regra 50/30/20
            BigDecimal needs = income.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal wants = income.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal savings = income.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
            
            result.put("success", true);
            result.put("monthlyIncome", income);
            result.put("needs", needs);
            result.put("wants", wants);
            result.put("savings", savings);
            result.put("rule", "50/30/20");
            
            // Detalhamento por categoria
            Map<String, BigDecimal> categories = new HashMap<>();
            categories.put("moradia", needs.multiply(new BigDecimal("0.40")));
            categories.put("alimentacao", needs.multiply(new BigDecimal("0.30")));
            categories.put("transporte", needs.multiply(new BigDecimal("0.20")));
            categories.put("outros_essenciais", needs.multiply(new BigDecimal("0.10")));
            categories.put("lazer", wants.multiply(new BigDecimal("0.50")));
            categories.put("compras", wants.multiply(new BigDecimal("0.30")));
            categories.put("entretenimento", wants.multiply(new BigDecimal("0.20")));
            categories.put("emergencia", savings.multiply(new BigDecimal("0.60")));
            categories.put("investimentos", savings.multiply(new BigDecimal("0.40")));
            
            result.put("categories", categories);
            
            log.info("✅ BudgetCalculator executado com sucesso para renda: {}", income);
            
        } catch (Exception e) {
            log.error("❌ Erro no BudgetCalculator: {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public boolean validateParameters(Map<String, Object> parameters) {
        if (!parameters.containsKey("monthlyIncome")) {
            return false;
        }
        
        try {
            BigDecimal income = new BigDecimal(parameters.get("monthlyIncome").toString());
            return income.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}

