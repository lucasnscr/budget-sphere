package com.budgetsphere.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 🛠️ Tool Registry - Tool Use Pattern
 * Registro central de todas as ferramentas financeiras disponíveis.
 * Permite descoberta e execução dinâmica de ferramentas.
 */
@Slf4j
@Component
public class ToolRegistry {
    
    private final Map<String, FinancialTool> tools = new HashMap<>();
    
    @Autowired
    public ToolRegistry(BudgetCalculatorTool budgetCalculator, 
                       ExpenseAnalyzerTool expenseAnalyzer) {
        registerTool(budgetCalculator);
        registerTool(expenseAnalyzer);
        
        log.info("🛠️ ToolRegistry inicializado com {} ferramentas", tools.size());
    }
    
    /**
     * Registra uma nova ferramenta
     */
    public void registerTool(FinancialTool tool) {
        tools.put(tool.getName(), tool);
        log.debug("✅ Ferramenta registrada: {}", tool.getName());
    }
    
    /**
     * Executa uma ferramenta pelo nome
     */
    public Map<String, Object> executeTool(String toolName, Map<String, Object> parameters) {
        log.info("🔧 Executando ferramenta: {} com parâmetros: {}", toolName, parameters);
        
        FinancialTool tool = tools.get(toolName);
        if (tool == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Ferramenta não encontrada: " + toolName);
            return error;
        }
        
        if (!tool.validateParameters(parameters)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Parâmetros inválidos para a ferramenta: " + toolName);
            return error;
        }
        
        return tool.execute(parameters);
    }
    
    /**
     * Lista todas as ferramentas disponíveis
     */
    public List<Map<String, String>> listTools() {
        List<Map<String, String>> toolList = new ArrayList<>();
        
        for (FinancialTool tool : tools.values()) {
            Map<String, String> toolInfo = new HashMap<>();
            toolInfo.put("name", tool.getName());
            toolInfo.put("description", tool.getDescription());
            toolList.add(toolInfo);
        }
        
        return toolList;
    }
    
    /**
     * Verifica se uma ferramenta existe
     */
    public boolean hasTool(String toolName) {
        return tools.containsKey(toolName);
    }
    
    /**
     * Obtém informações sobre uma ferramenta específica
     */
    public Map<String, String> getToolInfo(String toolName) {
        FinancialTool tool = tools.get(toolName);
        if (tool == null) {
            return null;
        }
        
        Map<String, String> info = new HashMap<>();
        info.put("name", tool.getName());
        info.put("description", tool.getDescription());
        return info;
    }
}

