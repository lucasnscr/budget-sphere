package com.budgetsphere.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 📊 Expense Analyzer Tool - Tool Use Pattern
 * Ferramenta para análise detalhada de despesas:
 * - Análise por categoria
 * - Identificação de padrões de gasto
 * - Detecção de anomalias
 * - Cálculo de tendências
 */
@Slf4j
@Component
public class ExpenseAnalyzerTool implements FinancialTool {
    
    @Override
    public String getName() {
        return "ExpenseAnalyzer";
    }
    
    @Override
    public String getDescription() {
        return "Analisa padrões de despesas e identifica anomalias";
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(Map<String, Object> parameters) {
        log.info("📊 Executando ExpenseAnalyzer com parâmetros: {}", parameters);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> expenses = (List<Map<String, Object>>) parameters.get("expenses");
            
            if (expenses == null || expenses.isEmpty()) {
                result.put("success", false);
                result.put("error", "Nenhuma despesa fornecida para análise");
                return result;
            }
            
            // Análise por categoria
            Map<String, BigDecimal> categoryTotals = analyzeByCategory(expenses);
            
            // Análise de padrões
            Map<String, Object> patterns = analyzePatterns(expenses);
            
            // Detecção de anomalias
            List<Map<String, Object>> anomalies = detectAnomalies(expenses);
            
            // Estatísticas gerais
            Map<String, Object> statistics = calculateStatistics(expenses);
            
            result.put("success", true);
            result.put("categoryTotals", categoryTotals);
            result.put("patterns", patterns);
            result.put("anomalies", anomalies);
            result.put("statistics", statistics);
            
            log.info("✅ ExpenseAnalyzer executado com sucesso para {} despesas", expenses.size());
            
        } catch (Exception e) {
            log.error("❌ Erro no ExpenseAnalyzer: {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, BigDecimal> analyzeByCategory(List<Map<String, Object>> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> (String) expense.get("category"),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                expense -> new BigDecimal(expense.get("amount").toString()),
                                BigDecimal::add
                        )
                ));
    }
    
    private Map<String, Object> analyzePatterns(List<Map<String, Object>> expenses) {
        Map<String, Object> patterns = new HashMap<>();
        
        // Maior categoria de gasto
        Map<String, BigDecimal> categoryTotals = analyzeByCategory(expenses);
        String topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        
        // Gasto médio por transação
        BigDecimal averageExpense = expenses.stream()
                .map(expense -> new BigDecimal(expense.get("amount").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(expenses.size()), 2, RoundingMode.HALF_UP);
        
        patterns.put("topCategory", topCategory);
        patterns.put("averageExpense", averageExpense);
        patterns.put("totalTransactions", expenses.size());
        
        return patterns;
    }
    
    private List<Map<String, Object>> detectAnomalies(List<Map<String, Object>> expenses) {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        
        // Calcular média e desvio padrão
        List<BigDecimal> amounts = expenses.stream()
                .map(expense -> new BigDecimal(expense.get("amount").toString()))
                .toList();
        
        BigDecimal average = amounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(amounts.size()), 2, RoundingMode.HALF_UP);
        
        // Detectar valores muito acima da média (anomalias)
        BigDecimal threshold = average.multiply(new BigDecimal("2.0"));
        
        for (Map<String, Object> expense : expenses) {
            BigDecimal amount = new BigDecimal(expense.get("amount").toString());
            if (amount.compareTo(threshold) > 0) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("expense", expense);
                anomaly.put("reason", "Valor muito acima da média");
                anomaly.put("threshold", threshold);
                anomalies.add(anomaly);
            }
        }
        
        return anomalies;
    }
    
    private Map<String, Object> calculateStatistics(List<Map<String, Object>> expenses) {
        Map<String, Object> stats = new HashMap<>();
        
        List<BigDecimal> amounts = expenses.stream()
                .map(expense -> new BigDecimal(expense.get("amount").toString()))
                .sorted()
                .toList();
        
        BigDecimal total = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = total.divide(new BigDecimal(amounts.size()), 2, RoundingMode.HALF_UP);
        BigDecimal min = amounts.getFirst();
        BigDecimal max = amounts.getLast();
        
        stats.put("total", total);
        stats.put("average", average);
        stats.put("min", min);
        stats.put("max", max);
        stats.put("count", amounts.size());
        
        return stats;
    }
    
    @Override
    public boolean validateParameters(Map<String, Object> parameters) {
        return parameters.containsKey("expenses") && parameters.get("expenses") instanceof List;
    }
}

