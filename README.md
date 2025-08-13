# Quick Start Guide

## 1. Build and Run
```bash
# Clone/create project directory
mkdir interview-prep-assistant
cd interview-prep-assistant

# Copy all code files to appropriate directories
# (Use the structure shown at the beginning)

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## 2. Test via curl commands

### Start Interview
```bash
curl -X POST http://localhost:8080/api/interview/start \
  -H "Content-Type: application/json" \
  -d '{"jobTitle":"Backend Engineer","topic":"System Design"}'
```

### Submit Response
```bash
curl -X POST http://localhost:8080/api/interview/respond \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"your-session-id","response":"I would use microservices architecture..."}'
```

### Check Session
```bash
curl http://localhost:8080/api/interview/session/your-session-id
```

### End Interview
```bash
curl -X POST http://localhost:8080/api/interview/end/your-session-id
```

## 3. Use Terminal Script
```bash
# Make script executable
chmod +x run_interview.sh

# Run interactive terminal client
./run_interview.sh
```

## 4. Example Full Interview Flow

1. Start interview with job title and topic
2. Receive first question
3. Provide detailed response
4. Get feedback and next question
5. Continue until satisfied
6. End interview session

## 5. Akka Patterns Demonstrated

- **Tell Pattern**: Async messaging to session storage
- **Ask Pattern**: Request-response for question generation
- **Forward Pattern**: Routing responses through evaluation pipeline
- **Cluster**: Distributed actors across multiple nodes

## 6. Architecture Benefits

- **Scalable**: Akka cluster handles load distribution
- **Resilient**: Actor supervision and cluster recovery
- **Responsive**: Async processing with Spring AI
- **Real-time**: Live interview simulation with instant feedback

## 7. Extend the System

- Add more sophisticated LLM prompts
- Implement different interview types
- Add persistence with database
- Create web frontend
- Add real-time WebSocket communication
- Implement user authentication