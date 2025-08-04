package com.budgetsphere.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Modelo de dados para orçamento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetData {
    private String userId;
    private BigDecimal monthlyIncome;
    private Map<String, BigDecimal> categories;
    private List<Expense> expenses;
    private LocalDate createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expense {
        private String description;
        private BigDecimal amount;
        private String category;
        private LocalDate date;
    }
}

