
# AI Interview Prep Assistant

An advanced, actor-based interview simulation platform leveraging Akka Typed, Spring Boot, and OpenAI (via Spring AI) to deliver realistic, adaptive interview experiences. Includes REST API, terminal client, and extensible architecture for custom interview flows and feedback.

## Features

- **AI-Generated Questions:** Dynamic interview questions tailored to job title and topic.
- **Conversational Flow:** Multi-turn Q&A with follow-up questions.
- **Performance Feedback:** At interview end, receive actionable feedback on strengths, weaknesses, and improvement areas.
- **Akka Actor System:** Scalable, resilient session and question management.
- **Terminal & API Clients:** Interact via REST endpoints or shell script client.
# AI Interview Prep Assistant

AI Interview Prep Assistant is a backend-first interview simulation platform that uses Akka Typed actors and Spring Boot to orchestrate multi-turn interviews powered by large language models (via Spring AI / OpenAI). It provides a REST API, a terminal demo client, and an extensible actor-based architecture for session management, question generation, and automated feedback.

Features
 - Adaptive, AI-driven interview questions
 - Actor-based session orchestration using Akka Typed
 - REST API for integration with web or CLI clients
 - Pluggable evaluation pipeline for LLM-based feedback

Tech stack
 - Java 17, Maven
 - Spring Boot, Spring AI
 - Akka Typed
 - OpenAI (configurable provider)

Quick start
1. Ensure Java 17+ and Maven are installed
2. Set OpenAI API key in environment or application config
3. Build and run:

   mvn clean package
   mvn spring-boot:run

Usage (examples)
- POST /api/interview/start — start a new interview session
- POST /api/interview/respond — submit an interview response
- POST /api/interview/end/{sessionId} — finish and receive feedback

Extending
- Add persistent storage (Postgres, MongoDB)
- Implement a web frontend (React/Vue) that calls the REST API
- Add authentication and user profile management

Contributing
- Open an issue to discuss significant changes
- Follow the code style and include tests for new functionality

License
MIT
