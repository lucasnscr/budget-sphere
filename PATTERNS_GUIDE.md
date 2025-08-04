# 🧠 Guia Completo dos Patterns de Agentes de IA

Este documento explica em detalhes cada pattern implementado no BudgetSphere, com exemplos práticos e casos de uso.

## 📋 **Índice de Patterns**

1. [ReAct Pattern](#react-pattern)
2. [Planning Pattern](#planning-pattern)
3. [Reflection Pattern](#reflection-pattern)
4. [Multi-Agent Pattern](#multi-agent-pattern)
5. [Tool Use Pattern](#tool-use-pattern)

---

## 🔄 **ReAct Pattern**

### **Conceito**
O ReAct (Reasoning and Acting) é um pattern que combina raciocínio e ação de forma iterativa. O agente observa, raciocina sobre o que observou, e então age baseado nesse raciocínio.

### **Implementação no BudgetSphere**

**Arquivo**: `ReactAgent.java`

```java
public AgentResponse process(String input, Map<String, Object> context) {
    // STEP 1: OBSERVE - Observar a situação
    String observation = observe(input, context);
    
    // STEP 2: REASON - Raciocinar sobre os dados
    String reasoning = reason(observation, context);
    
    // STEP 3: ACT - Decidir ação
    String action = act(reasoning, context);
    
    return createResponseWithReasoning(message, data, reasoning, action, true);
}
```

### **Quando Usar**
- Análise de despesas anômalas
- Detecção de padrões de gasto
- Alertas proativos
- Diagnóstico financeiro

### **Exemplo Prático**

**Entrada**: "Gastei R$ 2000 em alimentação este mês"

**Processo**:
1. **OBSERVE**: "O usuário gastou R$ 2000 em alimentação em um mês"
2. **REASON**: "Este valor está muito acima da média recomendada de 15% da renda para alimentação"
3. **ACT**: "Criar alerta e sugerir revisão dos hábitos alimentares"

**Teste**:
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "Gastei R$ 2000 em alimentação este mês"}'
```

### **Vantagens**
- Raciocínio explícito e auditável
- Decisões baseadas em lógica clara
- Fácil debug e melhoria
- Transparência no processo

---

## 📋 **Planning Pattern**

### **Conceito**
O Planning Pattern foca na criação de planos estruturados e otimizados. Analisa a situação atual, cria um plano detalhado e o otimiza baseado em melhores práticas.

### **Implementação no BudgetSphere**

**Arquivo**: `PlanningAgent.java`

```java
public AgentResponse process(String input, Map<String, Object> context) {
    // STEP 1: ANALYZE - Analisar situação atual
    String analysis = analyze(input, context);
    
    // STEP 2: PLAN - Criar plano estruturado
    String plan = createPlan(analysis, context);
    
    // STEP 3: OPTIMIZE - Otimizar o plano
    String optimizedPlan = optimize(plan, context);
    
    return createResponse(optimizedPlan, data, true);
}
```

### **Quando Usar**
- Criação de orçamentos mensais
- Planejamento de metas financeiras
- Estratégias de economia
- Planos de investimento

### **Exemplo Prático**

**Entrada**: "Quero economizar R$ 1000 por mês"

**Processo**:
1. **ANALYZE**: Analisa renda atual, gastos fixos, padrões de consumo
2. **PLAN**: Cria plano com metas específicas, estratégias e cronograma
3. **OPTIMIZE**: Ajusta o plano para ser mais realista e eficiente

**Teste**:
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/plan \
  -H "Content-Type: application/json" \
  -d '{"goal": "Quero economizar R$ 1000 por mês"}'
```

### **Características do Plano Gerado**
- **Objetivo Principal**: Meta clara e mensurável
- **Metas Específicas**: Valores e prazos definidos
- **Estratégias**: Como alcançar cada meta
- **Cronograma**: Passos mensais
- **Indicadores**: Como medir sucesso

---

## 🪞 **Reflection Pattern**

### **Conceito**
O Reflection Pattern implementa aprendizado contínuo através da análise retrospectiva. Revisa resultados passados, reflete sobre o que funcionou, e aprende para melhorar futuras decisões.

### **Implementação no BudgetSphere**

**Arquivo**: `ReflectionAgent.java`

```java
public AgentResponse process(String input, Map<String, Object> context) {
    // STEP 1: REVIEW - Revisar dados históricos
    String review = review(input, context);
    
    // STEP 2: REFLECT - Refletir sobre padrões
    String reflection = reflect(review, context);
    
    // STEP 3: LEARN - Extrair aprendizados
    String learning = learn(reflection, context);
    
    return createResponse(learning, data, true);
}
```

### **Quando Usar**
- Análise mensal/trimestral de resultados
- Melhoria contínua de estratégias
- Identificação de padrões comportamentais
- Ajuste de metas e planos

### **Exemplo Prático**

**Entrada**: "últimos 3 meses"

**Processo**:
1. **REVIEW**: Revisa gastos, metas atingidas, decisões tomadas
2. **REFLECT**: Identifica o que funcionou bem e o que não funcionou
3. **LEARN**: Extrai lições e define melhorias específicas

**Teste**:
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/reflect \
  -H "Content-Type: application/json" \
  -d '{"period": "últimos 3 meses"}'
```

### **Tipos de Reflexão**
- **Resultados Alcançados**: O que foi conquistado
- **Padrões Identificados**: Comportamentos recorrentes
- **Lições Aprendidas**: Insights importantes
- **Melhorias Futuras**: Ações concretas

---

## 👥 **Multi-Agent Pattern**

### **Conceito**
O Multi-Agent Pattern orquestra múltiplos agentes especializados para resolver problemas complexos. Um supervisor decide quais agentes usar e como combinar seus resultados.

### **Implementação no BudgetSphere**

**Arquivo**: `SupervisorAgent.java`

```java
public AgentResponse process(String input, Map<String, Object> context) {
    // STEP 1: ROUTE - Decidir qual agente usar
    String routing = route(input, context);
    
    // STEP 2: COORDINATE - Executar agentes apropriados
    List<AgentResponse> responses = coordinate(routing, input, context);
    
    // STEP 3: SYNTHESIZE - Combinar resultados
    String synthesis = synthesize(responses, context);
    
    return createResponse(synthesis, data, true);
}
```

### **Estratégias de Orquestração**
- **SINGLE**: Usa apenas um agente especializado
- **SEQUENTIAL**: Executa agentes em sequência
- **PARALLEL**: Executa múltiplos agentes simultaneamente

### **Quando Usar**
- Problemas complexos que requerem múltiplas perspectivas
- Situações que se beneficiam de diferentes tipos de análise
- Quando precisar combinar planejamento, análise e reflexão

### **Exemplo Prático**

**Entrada**: "Preciso de ajuda com meu orçamento mensal"

**Processo**:
1. **ROUTE**: Decide usar PlanningAgent + ReactAgent
2. **COORDINATE**: Executa ambos os agentes
3. **SYNTHESIZE**: Combina plano estruturado com análise situacional

**Teste**:
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Preciso de ajuda com meu orçamento mensal"}'
```

### **Vantagens**
- Combina especialidades diferentes
- Resultados mais completos e robustos
- Flexibilidade na escolha de estratégias
- Escalabilidade para novos agentes

---

## 🛠️ **Tool Use Pattern**

### **Conceito**
O Tool Use Pattern permite que agentes executem ferramentas especializadas para tarefas específicas. Implementa descoberta dinâmica, validação e execução segura de ferramentas.

### **Implementação no BudgetSphere**

**Arquivos**: 
- `FinancialTool.java` (interface)
- `BudgetCalculatorTool.java` (implementação)
- `ExpenseAnalyzerTool.java` (implementação)
- `ToolRegistry.java` (registro)

```java
public interface FinancialTool {
    String getName();
    String getDescription();
    Map<String, Object> execute(Map<String, Object> parameters);
    boolean validateParameters(Map<String, Object> parameters);
}
```

### **Ferramentas Implementadas**

#### 1. **BudgetCalculator**
- **Função**: Aplica regra 50/30/20 para distribuição de orçamento
- **Entrada**: `monthlyIncome`
- **Saída**: Distribuição detalhada por categorias

#### 2. **ExpenseAnalyzer**
- **Função**: Analisa padrões de despesas e detecta anomalias
- **Entrada**: Lista de despesas
- **Saída**: Estatísticas, padrões e anomalias

### **Exemplo Prático - BudgetCalculator**

**Teste**:
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/tools/BudgetCalculator \
  -H "Content-Type: application/json" \
  -d '{"monthlyIncome": "5000"}'
```

**Resposta**:
```json
{
  "success": true,
  "monthlyIncome": 5000,
  "needs": 2500.00,
  "wants": 1500.00,
  "savings": 1000.00,
  "rule": "50/30/20",
  "categories": {
    "moradia": 1000.00,
    "alimentacao": 750.00,
    "transporte": 500.00,
    "outros_essenciais": 250.00,
    "lazer": 750.00,
    "compras": 450.00,
    "entretenimento": 300.00,
    "emergencia": 600.00,
    "investimentos": 400.00
  }
}
```

### **Como Adicionar Nova Ferramenta**

1. **Implementar a interface**:
```java
@Component
public class NovaFerramenta implements FinancialTool {
    @Override
    public String getName() { return "NovaFerramenta"; }
    
    @Override
    public String getDescription() { return "Descrição da ferramenta"; }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> parameters) {
        // Implementação da lógica
    }
    
    @Override
    public boolean validateParameters(Map<String, Object> parameters) {
        // Validação dos parâmetros
    }
}
```

2. **Registrar no ToolRegistry**:
```java
@Autowired
public ToolRegistry(BudgetCalculatorTool budgetCalculator, 
                   ExpenseAnalyzerTool expenseAnalyzer,
                   NovaFerramenta novaFerramenta) {
    registerTool(budgetCalculator);
    registerTool(expenseAnalyzer);
    registerTool(novaFerramenta);
}
```

---

## 🔗 **Combinando Patterns**

### **Exemplo: Análise Completa de Orçamento**

1. **Tool Use**: Calcular distribuição ideal com BudgetCalculator
2. **ReAct**: Analisar gastos atuais vs distribuição ideal
3. **Planning**: Criar plano para ajustar gastos
4. **Reflection**: Revisar progresso após implementação

### **Fluxo Integrado**
```
Entrada do Usuário
       ↓
SupervisorAgent (Multi-Agent)
       ↓
┌─────────────┬─────────────┬─────────────┐
│ ReactAgent  │PlanningAgent│ReflectionAgent│
│ (Análise)   │ (Plano)     │ (Aprendizado) │
└─────────────┴─────────────┴─────────────┘
       ↓
Ferramentas (Tool Use)
       ↓
Resposta Sintetizada
```

---

## 📊 **Comparação dos Patterns**

| Pattern | Foco | Quando Usar | Saída Principal |
|---------|------|-------------|-----------------|
| **ReAct** | Análise + Ação | Problemas imediatos | Diagnóstico + Ação |
| **Planning** | Estruturação | Metas futuras | Plano detalhado |
| **Reflection** | Aprendizado | Melhoria contínua | Lições + Melhorias |
| **Multi-Agent** | Orquestração | Problemas complexos | Solução integrada |
| **Tool Use** | Execução | Cálculos específicos | Resultados precisos |

---

## 🎯 **Melhores Práticas**

### **Para Implementação**
1. **Mantenha cada pattern focado** em sua responsabilidade específica
2. **Use logging detalhado** para debug e auditoria
3. **Implemente validação** de entrada em todos os agentes
4. **Documente o raciocínio** de cada decisão

### **Para Uso**
1. **Escolha o pattern certo** para cada tipo de problema
2. **Combine patterns** para soluções mais robustas
3. **Use o SupervisorAgent** quando não souber qual pattern usar
4. **Monitore os logs** para entender o comportamento

### **Para Extensão**
1. **Siga as interfaces existentes** ao adicionar novos agentes
2. **Implemente testes** para cada novo pattern
3. **Mantenha a documentação** atualizada
4. **Use dependency injection** para facilitar testes

---

**Este guia demonstra como cada pattern resolve problemas específicos e como podem ser combinados para criar soluções mais poderosas e inteligentes.**

