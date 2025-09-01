# BudgetSphere - AI-Powered Financial Assistant with Memory

🤖 **Advanced AI Agent System for Personal Financial Management**

BudgetSphere is a sophisticated financial assistant powered by AI agents with integrated memory capabilities. The system uses Spring AI, Ollama, and PostgreSQL with PGVector to provide personalized, context-aware financial advice that improves over time through experience and learning.

## 🌟 Key Features

### 🧠 **Memory-Enabled AI Agents**
- **Episodic Memory**: Remembers past interactions and user experiences
- **Semantic Knowledge**: Maintains financial concepts and rules
- **Procedural Memory**: Optimizes workflows based on successful patterns
- **Vector Similarity Search**: Finds relevant past experiences and knowledge

### 🤖 **Intelligent Agent System**
- **SupervisorAgent**: Intelligent routing to the most appropriate agent
- **ReactAgent**: Quick responses and calculations
- **PlanningAgent**: Complex financial planning and strategies
- **ReflectionAgent**: Analysis and performance insights

### 🌐 **Multi-Language Support**
- English and Portuguese language support
- Automatic language detection and response matching

### 📊 **Comprehensive Monitoring**
- Real-time memory dashboard
- Performance metrics and analytics
- Health monitoring for all system components
- Grafana dashboards for advanced monitoring

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Request  │───▶│ SupervisorAgent │───▶│ Specialized     │
│                 │    │ (Routing)       │    │ Agents          │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Memory System   │◀───│ Memory          │───▶│ Vector Store    │
│ (PostgreSQL)    │    │ Orchestration   │    │ (PGVector)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Memory Architecture
- **Episodic Memory**: Stores user interactions with context and outcomes
- **Semantic Knowledge**: Financial concepts, rules, and definitions
- **Procedural Memory**: Optimized workflows and decision patterns
- **Vector Embeddings**: Enable semantic similarity search across all memory types

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 21
- Maven 3.6+
- 8GB+ RAM (for Ollama model)

### 1. Clone and Setup
```bash
git clone <repository-url>
cd budgetsphere-complete
```

### 2. Start the System
```bash
./scripts/start.sh
```

This script will:
- Start PostgreSQL with PGVector extension
- Launch Ollama and pull the Qwen2.5:7b model
- Start Redis, Prometheus, and Grafana
- Build and run the Spring Boot application

### 3. Verify Installation
```bash
./scripts/test.sh
```

### 4. Access the System
- **Main Application**: http://localhost:8080
- **Memory Dashboard**: http://localhost:8080/api/memory/dashboard
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Grafana Monitoring**: http://localhost:3000 (admin/budgetsphere123)

## 📡 API Usage

### Basic Chat Request
```bash
curl -X POST http://localhost:8080/api/agents/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "sessionId": "session001",
    "message": "How much should I save for retirement?",
    "useMemory": true,
    "storeExperience": true,
    "monthlyIncome": 5000.00,
    "monthlyExpenses": 3500.00,
    "age": 28,
    "riskTolerance": "MODERATE"
  }'
```

### Response Format
```json
{
  "sessionId": "session001",
  "userId": "user123",
  "agentUsed": "PlanningAgent",
  "message": "Based on your age and income, I recommend...",
  "success": true,
  "reasoning": "Selected PlanningAgent for retirement planning expertise",
  "recommendations": [
    "Start with 15% of income for retirement savings",
    "Maximize employer 401k match first"
  ],
  "nextSteps": [
    "Open a Roth IRA account",
    "Review investment allocation"
  ],
  "financialAnalysis": {
    "savingsRate": 0.30,
    "budgetUtilization": 0.70,
    "financialHealthScore": "Good"
  },
  "memoryInfo": {
    "confidenceScore": 0.85,
    "summary": "High confidence based on similar past interactions"
  },
  "processingTimeMs": 1250,
  "timestamp": "2024-01-15T10:30:00"
}
```

## 🎯 Agent Specializations

### SupervisorAgent
**Purpose**: Intelligent request routing
- Analyzes request content and context
- Routes to the most appropriate specialized agent
- Considers user history and agent performance

### ReactAgent
**Purpose**: Quick responses and calculations
- Immediate financial questions
- Mathematical calculations
- General financial advice
- Emergency fund calculations

### PlanningAgent
**Purpose**: Strategic financial planning
- Retirement planning
- Investment strategies
- Long-term goal planning
- Complex financial scenarios

### ReflectionAgent
**Purpose**: Analysis and insights
- Performance analysis
- Progress tracking
- Financial health assessment
- Trend identification

## 🧠 Memory System

### Episodic Memory
Stores individual user interactions with full context:
```json
{
  "userId": "user123",
  "sessionId": "session001",
  "agentName": "PlanningAgent",
  "userMessage": "Help me plan for retirement",
  "agentResponse": "Based on your profile...",
  "contextData": {
    "monthlyIncome": 5000,
    "age": 28,
    "riskTolerance": "MODERATE"
  },
  "satisfactionScore": 0.85,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Semantic Knowledge
Financial concepts and rules:
```json
{
  "concept": "Emergency Fund",
  "category": "savings",
  "definition": "Money set aside for unexpected expenses",
  "rules": [
    "Should cover 3-6 months of expenses",
    "Keep in easily accessible account"
  ],
  "confidenceScore": 0.95,
  "usageCount": 150
}
```

### Procedural Memory
Optimized workflows:
```json
{
  "procedureName": "Emergency Fund Assessment",
  "agentPattern": "ReactAgent",
  "steps": [
    "Calculate monthly essential expenses",
    "Multiply by 3-6 months",
    "Compare with current savings",
    "Recommend specific target"
  ],
  "successRate": 0.92,
  "efficiencyScore": 0.85
}
```

## 📊 Monitoring and Analytics

### Memory Dashboard
Access the comprehensive memory dashboard at `/api/memory/dashboard`:
- System health overview
- Memory distribution charts
- Recent activity logs
- User-specific analytics
- Performance trends

### Metrics Available
- **Success Rate**: Percentage of successful interactions
- **Satisfaction Score**: Average user satisfaction (0.0-1.0)
- **Memory Health**: Overall system memory health
- **Agent Performance**: Individual agent effectiveness
- **Response Time**: Average processing time

### Grafana Dashboards
Pre-configured dashboards for:
- Application performance metrics
- Memory system health
- Agent usage patterns
- User engagement analytics

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/budgetsphere
SPRING_DATASOURCE_USERNAME=budgetsphere
SPRING_DATASOURCE_PASSWORD=budgetsphere123

# Ollama Configuration
SPRING_AI_OLLAMA_BASE_URL=http://localhost:11434
SPRING_AI_OLLAMA_CHAT_MODEL=qwen2.5:7b

# Redis Configuration
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=budgetsphere123

# Memory Configuration
BUDGETSPHERE_MEMORY_VECTOR_DIMENSION=1536
BUDGETSPHERE_MEMORY_SIMILARITY_THRESHOLD=0.7
BUDGETSPHERE_MEMORY_MAX_EPISODES_PER_USER=1000
```

### Application Properties
Key configuration options in `application.yml`:
- Memory system parameters
- Agent routing preferences
- Performance thresholds
- Monitoring settings

## 🧪 Testing

### Automated Test Suite
```bash
./scripts/test.sh
```

Tests include:
- Health checks for all services
- Agent functionality verification
- Memory system operations
- Multi-language support
- Complex financial scenarios
- Performance benchmarks

### Manual Testing
Use the provided example payloads in the `/examples` directory:
- Basic financial questions
- Complex planning scenarios
- Multi-session conversations
- Portuguese language examples

## 🛠️ Development

### Project Structure
```
budgetsphere-complete/
├── src/main/java/com/budgetsphere/
│   ├── agent/              # AI Agent implementations
│   ├── controller/         # REST API controllers
│   ├── dto/               # Data transfer objects
│   ├── memory/            # Memory system services
│   ├── model/             # JPA entities
│   └── repository/        # Data repositories
├── src/main/resources/
│   ├── templates/         # Thymeleaf templates
│   ├── static/           # Static web resources
│   └── application.yml   # Configuration
├── scripts/              # Utility scripts
├── config/              # External configuration
└── docker-compose.yml  # Infrastructure setup
```

### Adding New Agents
1. Extend `BaseAgent` class
2. Implement required abstract methods
3. Add to `SupervisorAgent` routing logic
4. Update configuration and tests

### Extending Memory System
1. Create new memory entity
2. Add repository interface
3. Update `MemoryOrchestrationService`
4. Add API endpoints if needed

## 🔒 Security Considerations

### Data Protection
- User data is isolated by userId
- Sensitive information is not logged
- Memory data includes privacy controls
- Vector embeddings don't contain raw text

### API Security
- Input validation on all endpoints
- Rate limiting for chat endpoints
- Session management for user tracking
- Error handling without information leakage

## 📈 Performance Optimization

### Memory Management
- Automatic cleanup of old episodes
- Vector index optimization
- Query performance monitoring
- Memory usage alerts

### Caching Strategy
- Redis for session data
- In-memory caching for frequent queries
- Vector similarity result caching
- Agent response caching

## 🚀 Deployment

### Production Considerations
- Use external PostgreSQL cluster
- Configure Ollama with GPU support
- Set up proper monitoring and alerting
- Implement backup strategies
- Configure load balancing

### Scaling Options
- Horizontal scaling of application instances
- Database read replicas
- Distributed vector storage
- Microservice decomposition

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Set up development environment
3. Run tests to ensure everything works
4. Make your changes
5. Add tests for new functionality
6. Submit a pull request

### Code Standards
- Follow Spring Boot best practices
- Use Lombok for boilerplate reduction
- Write comprehensive tests
- Document new features
- Follow semantic versioning

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

### Common Issues
- **Ollama model not loading**: Ensure sufficient RAM (8GB+)
- **Database connection errors**: Check PostgreSQL container status
- **Slow responses**: Monitor system resources and Ollama performance
- **Memory dashboard not loading**: Verify all services are running

### Getting Help
- Check the troubleshooting section
- Review application logs
- Run the test suite to identify issues
- Check Docker container status

### Logs and Debugging
```bash
# Application logs
tail -f logs/application.log

# Docker service logs
docker-compose logs -f

# Database logs
docker-compose logs postgres

# Ollama logs
docker-compose logs ollama
```

---

**Built with ❤️ using Spring AI, Ollama, and PostgreSQL**

*BudgetSphere - Making financial planning intelligent, personal, and continuously improving.*

