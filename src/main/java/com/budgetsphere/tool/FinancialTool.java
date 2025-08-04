package com.budgetsphere.tool;

import java.util.Map;

/**
 * Interface para ferramentas financeiras (Tool Use Pattern)
 */
public interface FinancialTool {
    
    /**
     * Nome da ferramenta
     */
    String getName();
    
    /**
     * Descrição do que a ferramenta faz
     */
    String getDescription();
    
    /**
     * Executa a ferramenta com os parâmetros fornecidos
     */
    Map<String, Object> execute(Map<String, Object> parameters);
    
    /**
     * Valida se os parâmetros são válidos para esta ferramenta
     */
    boolean validateParameters(Map<String, Object> parameters);
}

