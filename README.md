# 🤖 BudgetSphere - Agentes de IA Financeira

**Versão Simplificada e Funcional**

Uma implementação prática e demonstrativa de **todos os patterns de agentes de IA** aplicados ao contexto financeiro pessoal, usando Spring AI 1.0.0-M6 com modelo qwen:8b.

## 🎯 **Objetivo**

Demonstrar de forma clara e funcional como implementar patterns de agentes de IA em uma aplicação real, focando na **simplicidade** e **funcionalidade** sem complexidades desnecessárias.

## 🧠 **Patterns Implementados**

### 1. 🔄 **ReAct Pattern** - `ReactAgent`
**Problema que resolve**: Análise inteligente com raciocínio explícito
**Solução**: Ciclo Observe → Reason → Act

```java
// Exemplo de uso
POST /api/v1/budgetsphere/analyze
{
  "input": "Gastei R$ 2000 em alimentação este mês"
}
```

**Como funciona**:
1. **OBSERVE**: Analisa os dados financeiros objetivamente
2. **REASON**: Raciocina sobre padrões e anomalias
3. **ACT**: Decide ação específica baseada no raciocínio

### 2. 📋 **Planning Pattern** - `PlanningAgent`
**Problema que resolve**: Criação de planos financeiros estruturados
**Solução**: Analyze → Plan → Optimize

```java
// Exemplo de uso
POST /api/v1/budgetsphere/plan
{
  "goal": "Quero economizar R$ 1000 por mês"
}
```

**Como funciona**:
1. **ANALYZE**: Analisa situação financeira atual
2. **PLAN**: Cria plano detalhado com metas e estratégias
3. **OPTIMIZE**: Otimiza o plano baseado em melhores práticas

### 3. 🪞 **Reflection Pattern** - `ReflectionAgent`
**Problema que resolve**: Aprendizado contínuo e melhoria
**Solução**: Review → Reflect → Learn

```java
// Exemplo de uso
POST /api/v1/budgetsphere/reflect
{
  "period": "últimos 3 meses"
}
```

**Como funciona**:
1. **REVIEW**: Revisa dados e resultados históricos
2. **REFLECT**: Reflete sobre o que funcionou e o que não funcionou
3. **LEARN**: Extrai aprendizados e define melhorias

### 4. 👥 **Multi-Agent Pattern** - `SupervisorAgent`
**Problema que resolve**: Orquestração inteligente de múltiplos agentes
**Solução**: Route → Coordinate → Synthesize

```java
// Exemplo de uso
POST /api/v1/budgetsphere/chat
{
  "message": "Preciso de ajuda com meu orçamento mensal"
}
```

**Como funciona**:
1. **ROUTE**: Decide quais agentes usar baseado na solicitação
2. **COORDINATE**: Executa agentes apropriados em sequência ou paralelo
3. **SYNTHESIZE**: Combina resultados em resposta unificada

### 5. 🛠️ **Tool Use Pattern** - `ToolRegistry`
**Problema que resolve**: Execução de ferramentas especializadas
**Solução**: Registro dinâmico e execução segura de ferramentas

```java
// Exemplo de uso
POST /api/v1/budgetsphere/tools/BudgetCalculator
{
  "monthlyIncome": "5000"
}
```

**Ferramentas disponíveis**:
- **BudgetCalculator**: Aplica regra 50/30/20 para distribuição de orçamento
- **ExpenseAnalyzer**: Analisa padrões de despesas e detecta anomalias

## 🚀 **Como Executar**

### Pré-requisitos
- **Java 21**
- **Maven 3.6+**
- **Ollama** com modelo `qwen:8b` (opcional para teste completo)

### Passos

1. **Clone/Download do projeto**
```bash
# Projeto está em: /home/ubuntu/BudgetSphere-Simple-v2
```

2. **Compile e execute**
```bash
mvn compile
mvn install -DskipTests
java -jar target/budget-sphere-agents-1.0.0.jar
```

3. **Teste a aplicação**
```bash
# Verificar status
curl http://localhost:8080/api/v1/budgetsphere/health

# Ver demonstração
curl http://localhost:8080/api/v1/budgetsphere/demo
```

## 📊 **Endpoints Principais**

### Informações Gerais
- `GET /api/v1/budgetsphere/` - Informações da aplicação
- `GET /api/v1/budgetsphere/health` - Status de saúde
- `GET /api/v1/budgetsphere/demo` - Exemplos de uso

### Agentes (Patterns)
- `POST /api/v1/budgetsphere/chat` - **Multi-Agent Pattern**
- `POST /api/v1/budgetsphere/analyze` - **ReAct Pattern**
- `POST /api/v1/budgetsphere/plan` - **Planning Pattern**
- `POST /api/v1/budgetsphere/reflect` - **Reflection Pattern**

### Ferramentas (Tool Use Pattern)
- `GET /api/v1/budgetsphere/tools` - Lista ferramentas
- `POST /api/v1/budgetsphere/tools/{toolName}` - Executa ferramenta

### Metadados
- `GET /api/v1/budgetsphere/agents` - Lista agentes
- `GET /api/v1/budgetsphere/agents/{agentName}` - Info do agente

## 🏗️ **Arquitetura Simplificada**

```
BudgetSphere-Simple-v2/
├── src/main/java/com/budgetsphere/
│   ├── agent/                    # 🤖 Agentes com patterns
│   │   ├── BaseAgent.java        # Classe base comum
│   │   ├── ReactAgent.java       # ReAct Pattern
│   │   ├── PlanningAgent.java    # Planning Pattern
│   │   ├── ReflectionAgent.java  # Reflection Pattern
│   │   └── SupervisorAgent.java  # Multi-Agent Pattern
│   ├── tool/                     # 🛠️ Ferramentas (Tool Use)
│   │   ├── FinancialTool.java    # Interface base
│   │   ├── BudgetCalculatorTool.java
│   │   ├── ExpenseAnalyzerTool.java
│   │   └── ToolRegistry.java     # Registro de ferramentas
│   ├── model/                    # 📊 Modelos de dados
│   ├── service/                  # ⚙️ Serviços
│   ├── controller/               # 🌐 REST Controllers
│   └── BudgetSphereApplication.java
├── src/main/resources/
│   └── application.yml           # Configuração simples
└── pom.xml                       # Dependências mínimas
```

## 🎯 **Características Principais**

### ✅ **Simplicidade**
- Código limpo e bem documentado
- Dependências mínimas necessárias
- Configuração simplificada

### ✅ **Funcionalidade**
- Todos os patterns funcionando
- Endpoints REST testados
- Logs detalhados para debug

### ✅ **Demonstrativo**
- Cada pattern claramente implementado
- Exemplos práticos de uso
- Documentação explicativa

### ✅ **Extensibilidade**
- Fácil adição de novos agentes
- Sistema de ferramentas plugável
- Arquitetura modular

## 🧪 **Exemplos de Teste**

### 1. Tool Use Pattern - Calculadora de Orçamento
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
    "lazer": 750.00,
    "emergencia": 600.00,
    "investimentos": 400.00
  }
}
```

### 2. Multi-Agent Pattern - Chat Inteligente
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Preciso de ajuda com meu orçamento mensal"}'
```

### 3. ReAct Pattern - Análise com Raciocínio
```bash
curl -X POST http://localhost:8080/api/v1/budgetsphere/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "Gastei R$ 2000 em alimentação este mês"}'
```

## 🔧 **Configuração**

### application.yml
```yaml
spring:
  application:
    name: budgetsphere-agents
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: qwen:8b
        options:
          temperature: 0.7
          max-tokens: 1000

server:
  port: 8080

logging:
  level:
    com.budgetsphere: DEBUG
```

## 📈 **Logs e Monitoramento**

A aplicação fornece logs detalhados para acompanhar a execução dos patterns:

```
🤖 ReactAgent chamando IA com prompt: ...
🔄 ReactAgent iniciando ciclo ReAct para: ...
👁️ OBSERVE: ...
🧠 REASON: ...
⚡ ACT: ...
```

## 🎓 **Aprendizados e Insights**

### **Por que esta implementação é eficaz?**

1. **Clareza**: Cada pattern é implementado de forma isolada e clara
2. **Funcionalidade**: Todos os endpoints funcionam e podem ser testados
3. **Simplicidade**: Sem complexidades desnecessárias
4. **Demonstrativo**: Fácil de entender e modificar
5. **Extensível**: Base sólida para expansões futuras

### **Diferenças da versão anterior**

- ❌ Removida complexidade de resiliência (Resilience4j)
- ❌ Removida infraestrutura pesada (PostgreSQL, Redis)
- ❌ Removidas dependências desnecessárias
- ✅ Foco nos patterns de agentes
- ✅ Código mais limpo e direto
- ✅ Execução local garantida
- ✅ Documentação clara dos patterns

## 🚀 **Próximos Passos**

Para expandir esta implementação:

1. **Adicionar novos agentes** seguindo o padrão `BaseAgent`
2. **Criar novas ferramentas** implementando `FinancialTool`
3. **Implementar memória persistente** (se necessário)
4. **Adicionar validações** e tratamento de erros
5. **Criar interface web** para demonstração visual

---

**Esta implementação demonstra que é possível criar agentes de IA funcionais e práticos sem complexidade excessiva, focando no que realmente importa: os patterns e sua aplicação efetiva.**

