package com.budgetsphere.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.ollama.management.PullModelStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.DefaultResponseErrorHandler;

import javax.sql.DataSource;
import java.time.Duration;

/**
 * 🤖 Spring AI Configuration
 * Configuração completa do Spring AI para o BudgetSphere
 */
@Configuration
public class SpringAIConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.model}")
    private String defaultChatModel;

    @Value("${budgetsphere.agents.default-temperature:0.1}")
    private Double defaultTemperature;

    @Value("${budgetsphere.agents.max-response-tokens:2048}")
    private Integer maxTokens;

    @Value("${budgetsphere.memory.vector-dimension:1536}")
    private Integer vectorDimension;

    /**
     * Configuração principal do modelo de chat Ollama
     */
    @Bean
    @Primary
    public ChatModel chatModel(
            ToolCallingManager toolCallingManager,
            ObservationRegistry observationRegistry,
            ModelManagementOptions modelManagementOptions,
            OllamaApi ollamaApi) {
        return new OllamaChatModel(
                ollamaApi,
                OllamaOptions.builder()
                        .model(defaultChatModel)
                        .temperature(defaultTemperature)
                        .topP(0.9)
                        .numPredict(maxTokens)
                        .repeatPenalty(1.1)
                        .build(),
                toolCallingManager,
                observationRegistry,
                modelManagementOptions);
    }

    /**
     * Modelo de embeddings para busca vetorial
     */
    @Bean
    public EmbeddingModel embeddingModel(OllamaApi ollamaApi,
                                         ObservationRegistry observationRegistry,
                                         ModelManagementOptions modelManagementOptions) {
        return new OllamaEmbeddingModel(
                ollamaApi,
                OllamaOptions.builder()
                        .model("nomic-embed-text")
                        .build(),
                observationRegistry,
                modelManagementOptions);
    }

    /**
     * Cliente de chat principal com configuração específica para assistente financeiro
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    Você é um assistente financeiro especializado em orçamento pessoal e planejamento financeiro.
                    
                    DIRETRIZES PRINCIPAIS:
                    - Seja prático, objetivo e forneça conselhos financeiros sólidos
                    - Use linguagem clara e acessível para todos os níveis de conhecimento financeiro
                    - Sempre considere a situação financeira brasileira (impostos, inflação, produtos financeiros locais)
                    - Baseie suas recomendações em princípios financeiros comprovados
                    - Considere o perfil de risco e objetivos específicos do usuário
                    - Forneça exemplos práticos e passos acionáveis
                    
                    ESPECIALIDADES:
                    - Orçamento pessoal e controle de gastos
                    - Planejamento de aposentadoria
                    - Estratégias de investimento
                    - Gestão de dívidas
                    - Reserva de emergência
                    - Planejamento tributário
                    - Educação financeira
                    
                    FORMATO DE RESPOSTA:
                    - Comece com uma resposta direta à pergunta
                    - Forneça recomendações específicas e acionáveis
                    - Inclua próximos passos quando apropriado
                    - Use exemplos numéricos quando relevante
                    - Mantenha um tom profissional mas acessível
                    """)
                .build();
    }

    /**
     * Cliente de chat para o ReactAgent - respostas rápidas
     */
    @Bean("reactChatClient")
    public ChatClient reactChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    Você é um assistente financeiro especializado em respostas rápidas e cálculos financeiros.
                    
                    FOCO: Respostas diretas, cálculos precisos e conselhos imediatos.
                    
                    ESPECIALIDADES:
                    - Cálculos de juros compostos
                    - Análise de orçamento
                    - Comparação de produtos financeiros
                    - Dicas de economia
                    - Conceitos financeiros básicos
                    
                    ESTILO: Direto, prático e educativo.
                    """)
                .build();
    }

    /**
     * Cliente de chat para o PlanningAgent - planejamento estratégico
     */
    @Bean("planningChatClient")
    public ChatClient planningChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    Você é um especialista em planejamento financeiro estratégico de longo prazo.
                    
                    FOCO: Planos detalhados, estratégias complexas e análise profunda.
                    
                    ESPECIALIDADES:
                    - Planejamento de aposentadoria
                    - Estratégias de investimento
                    - Planejamento patrimonial
                    - Objetivos financeiros de longo prazo
                    - Análise de cenários
                    
                    ESTILO: Estruturado, detalhado e estratégico.
                    """)
                .build();
    }

    /**
     * Cliente de chat para o ReflectionAgent - análise e insights
     */
    @Bean("reflectionChatClient")
    public ChatClient reflectionChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    Você é um analista financeiro especializado em avaliação de performance e insights.
                    
                    FOCO: Análise crítica, identificação de padrões e recomendações de melhoria.
                    
                    ESPECIALIDADES:
                    - Análise de performance financeira
                    - Identificação de tendências
                    - Avaliação de progresso
                    - Otimização de estratégias
                    - Insights comportamentais
                    
                    ESTILO: Analítico, perspicaz e orientado a resultados.
                    """)
                .build();
    }

    /**
     * Vector Store para busca semântica na memória
     */
    @Bean
    public VectorStore vectorStore(DataSource dataSource, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(new JdbcTemplate(dataSource), embeddingModel)
                .dimensions(vectorDimension)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10000)
                .build();
    }

    /**
     * Memória de chat em memória para sessões temporárias
     */
    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    /**
     * Opções de gerenciamento de modelos
     */
    @Bean
    public ModelManagementOptions modelManagementOptions() {
        return ModelManagementOptions.builder()
                .pullModelStrategy(PullModelStrategy.WHEN_MISSING)
                .timeout(Duration.ofMinutes(10))
                .maxRetries(3)
                .build();
    }

    /**
     * API do Ollama para operações diretas
     */
    @Bean
    public OllamaApi ollamaApi(RestClient.Builder restClientBuilder,
                               WebClient.Builder webClientBuilder) {
        return OllamaApi.builder()
                .baseUrl(this.ollamaBaseUrl)
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
    }
}
