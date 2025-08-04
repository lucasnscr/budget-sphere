package com.budgetsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BudgetSphere - Assistente Financeiro com Agentes de IA
 * 
 * Esta aplicação demonstra a implementação de todos os patterns de agentes:
 * - ReAct Pattern
 * - Planning Pattern  
 * - Plan-and-Execute Pattern
 * - Reflection Pattern
 * - Multi-Agent Pattern
 * - Sequential Workflow
 * - Tool Use Pattern
 * - Loop de Melhoria
 * - Agentic Memory
 */
@SpringBootApplication
public class BudgetSphereApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando BudgetSphere - Agentes de IA Financeira");
        System.out.println("📊 Patterns implementados: ReAct, Planning, Reflection, Multi-Agent, Tool Use");
        SpringApplication.run(BudgetSphereApplication.class, args);
    }
}

