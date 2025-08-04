package com.budgetsphere.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Resposta padrão dos agentes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private String agentName;
    private String pattern;
    private String message;
    private Map<String, Object> data;
    private boolean success;
    private LocalDateTime timestamp;
    private String reasoning;
    private String nextAction;
}

