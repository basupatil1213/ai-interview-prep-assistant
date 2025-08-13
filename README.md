
# AI Interview Prep Assistant

An advanced, actor-based interview simulation platform leveraging Akka Typed, Spring Boot, and OpenAI (via Spring AI) to deliver realistic, adaptive interview experiences. Includes REST API, terminal client, and extensible architecture for custom interview flows and feedback.

## Features

- **AI-Generated Questions:** Dynamic interview questions tailored to job title and topic.
- **Conversational Flow:** Multi-turn Q&A with follow-up questions.
- **Performance Feedback:** At interview end, receive actionable feedback on strengths, weaknesses, and improvement areas.
- **Akka Actor System:** Scalable, resilient session and question management.
- **Terminal & API Clients:** Interact via REST endpoints or shell script client.

## Getting Started

### Prerequisites
- Java 17+
- Maven
- OpenAI API Key (set in environment or application config)

### Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### API Usage

**Start Interview**
```bash
curl -X POST http://localhost:8080/api/interview/start \
  -H "Content-Type: application/json" \
  -d '{"jobTitle":"Backend Engineer","topic":"System Design"}'
```

**Submit Response**
```bash
curl -X POST http://localhost:8080/api/interview/respond \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"your-session-id","response":"I would use microservices architecture..."}'
```

**Check Session**
```bash
curl http://localhost:8080/api/interview/session/your-session-id
```

**End Interview & Get Feedback**
```bash
curl -X POST http://localhost:8080/api/interview/end/your-session-id
```

### Terminal Client
```bash
chmod +x demoscript.sh
./demoscript.sh
```

## Architecture Overview

- **Akka Typed Actors:**
  - SessionStorageActor: Manages interview sessions
  - QuestionGeneratorActor: Produces AI questions
  - EvaluationActor: Handles response evaluation
  - InterviewManagerActor: Orchestrates interview flow
- **Spring Boot REST API:** Exposes endpoints for interview lifecycle
- **Spring AI:** Integrates with OpenAI for question and feedback generation

## Akka Patterns Used
- Tell: Async messaging
- Ask: Request-response
- Forward: Pipeline routing
- Cluster: Distributed actors

## Extending the System

- Add new LLM prompts or interview types
- Integrate database persistence
- Build a React/Tailwind web frontend
- Add real-time WebSocket communication
- Implement authentication & user management

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss your ideas.

## License

MIT License