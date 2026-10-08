# AI Interview Prep Assistant

A backend-first interview simulation platform that uses **Akka Typed** actors and **Spring Boot** to orchestrate realistic, multi-turn interviews powered by large language models (via **Spring AI** / OpenAI). It exposes a REST API, ships with a terminal demo client, and has an extensible actor-based architecture for session management, question generation, and automated feedback.

## Features

- **AI-generated questions** – dynamic interview questions tailored to the job title and topic
- **Conversational flow** – multi-turn Q&A with AI-generated follow-up questions
- **Performance feedback** – at the end of an interview, get actionable feedback on strengths, weaknesses, and areas to improve
- **Akka actor system** – scalable, resilient session and question management
- **Terminal & API clients** – interact through the REST endpoints or the `demoscript.sh` shell client

## Architecture

| Component | Responsibility |
|---|---|
| `InterviewController` | REST API under `/api/interview` |
| `InterviewManagerActor` | Coordinates the interview flow |
| `AiQuestionService` | Generates questions, follow-ups and end-of-interview feedback with Spring AI (`gpt-4o-mini`) |
| `QuestionGeneratorActor` | Actor-based question generator with a built-in fallback question bank |
| `EvaluationActor` | Per-answer evaluation hook (currently simulated scoring; extension point for LLM-based scoring) |
| `SessionStorageActor` | Holds interview sessions in memory |
| `ClusterManager`, `AkkaConfiguration`, `ClusterConfiguration` | Akka Typed / Akka Cluster setup |

## Tech stack

- Java 17, Maven
- Spring Boot 3, Spring AI
- Akka Typed (actor + cluster)
- OpenAI (`gpt-4o-mini` by default)

## Quick start

1. Make sure Java 17+ and Maven are installed.
2. Provide your OpenAI API key through the `openai.api.key` property, for example:

   ```bash
   export OPENAI_API_KEY=sk-...
   ```

3. Build and run:

   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

The API is served at `http://localhost:8080/api/interview`.

## API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/interview/start` | Start a session. Body: `{"jobTitle": "Backend Engineer", "topic": "System Design"}` |
| `POST` | `/api/interview/respond` | Submit an answer. Body: `{"sessionId": "...", "response": "..."}` |
| `GET` | `/api/interview/session/{sessionId}` | Get the current session state |
| `POST` | `/api/interview/end/{sessionId}` | End the interview and receive feedback |

### Terminal client

With the server running (requires `curl` and `jq`):

```bash
./demoscript.sh
```

## Extending

- Add persistent storage (Postgres, MongoDB)
- Build a web frontend (React/Vue) that calls the REST API
- Add authentication and user profile management

## Contributing

- Open an issue to discuss significant changes
- Follow the existing code style and include tests for new functionality

## License

Apache License 2.0 – see [LICENSE](LICENSE).
