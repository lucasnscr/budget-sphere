package com.budgetsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * BudgetSphere Application - Complete AI-powered financial planning system
 * with intelligent agents and memory integration.
 * 
 * Features:
 * - Multi-agent architecture (ReactAgent, PlanningAgent, ReflectionAgent, SupervisorAgent)
 * - Comprehensive memory system (Episodic, Semantic, Procedural, Short-term)
 * - Spring AI integration with Ollama
 * - Vector database for semantic search
 * - RESTful APIs for agent interaction
 * - Real-time memory monitoring dashboard
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class BudgetSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetSphereApplication.class, args);
        
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════════╗
            ║                    BudgetSphere Complete                     ║
            ║              AI-Powered Financial Planning System            ║
            ╠══════════════════════════════════════════════════════════════╣
            ║                                                              ║
            ║  🤖 Multi-Agent Architecture:                                ║
            ║     • ReactAgent - Reactive financial analysis              ║
            ║     • PlanningAgent - Strategic financial planning          ║
            ║     • ReflectionAgent - Performance analysis & insights     ║
            ║     • SupervisorAgent - Intelligent request routing         ║
            ║                                                              ║
            ║  🧠 Memory System:                                           ║
            ║     • Episodic Memory - User interaction history            ║
            ║     • Semantic Memory - Financial knowledge base            ║
            ║     • Procedural Memory - Optimized workflows               ║
            ║     • Short-term Memory - Session context                   ║
            ║                                                              ║
            ║  🔗 Available Endpoints:                                     ║
            ║     • POST /api/agents/chat - Main agent interaction        ║
            ║     • GET  /api/memory/dashboard - Memory monitoring        ║
            ║     • GET  /api/agents/status - Agent health status         ║
            ║     • GET  /swagger-ui.html - API Documentation             ║
            ║                                                              ║
            ║  📊 Dashboard: http://localhost:8080/dashboard               ║
            ║  📚 API Docs: http://localhost:8080/swagger-ui.html         ║
            ║                                                              ║
            ╚══════════════════════════════════════════════════════════════╝
            
            """);
    }
}

