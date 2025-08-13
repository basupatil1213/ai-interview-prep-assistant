package com.interviewprep.controllers;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import com.interviewprep.actors.ClusterManager;
import com.interviewprep.actors.InterviewManagerActor;
import com.interviewprep.actors.SessionStorageActor;
import com.interviewprep.models.*;
import com.interviewprep.services.AiQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class InterviewController {

    @Autowired
    private ActorSystem<ClusterManager.Command> actorSystem;

    // Create actors only once and reuse them
    private final ActorRef<SessionStorageActor.Command> sessionStorage;
    private final ActorRef<com.interviewprep.actors.QuestionGeneratorActor.Command> questionGenerator;
    private final ActorRef<com.interviewprep.actors.EvaluationActor.Command> evaluationActor;
    private final ActorRef<InterviewManagerActor.Command> interviewManager;
    private final AiQuestionService aiQuestionService;

    @Autowired
    public InterviewController(ActorSystem<ClusterManager.Command> actorSystem, AiQuestionService aiQuestionService) {
        this.sessionStorage = actorSystem.systemActorOf(SessionStorageActor.create(), "session-storage", akka.actor.typed.Props.empty());
        this.questionGenerator = actorSystem.systemActorOf(com.interviewprep.actors.QuestionGeneratorActor.create(), "question-generator", akka.actor.typed.Props.empty());
        this.evaluationActor = actorSystem.systemActorOf(com.interviewprep.actors.EvaluationActor.create(), "evaluation-actor", akka.actor.typed.Props.empty());
        this.interviewManager = actorSystem.systemActorOf(
            InterviewManagerActor.create(questionGenerator, evaluationActor, sessionStorage),
            "interview-manager",
            akka.actor.typed.Props.empty()
        );
        this.aiQuestionService = aiQuestionService;
    }

    @PostMapping("/start")
    public ResponseEntity<InterviewSession> startInterview(@RequestBody StartInterviewRequest request) {
        System.out.println("🚀 Starting new interview: " + request.getJobTitle() + " - " + request.getTopic());
        String aiQuestion = aiQuestionService.generateQuestion(request.getJobTitle(), request.getTopic(), 1);
        InterviewSession session = new InterviewSession(request.getJobTitle(), request.getTopic());
        session.setCurrentQuestion(aiQuestion);
        // Store session
        sessionStorage.tell(new SessionStorageActor.StoreSession(session));
        System.out.println("✅ Interview session created: " + session.getSessionId());
        System.out.println("❓ First question: " + session.getCurrentQuestion());
        return ResponseEntity.ok(session);
    }

    @PostMapping("/respond")
    public ResponseEntity<InterviewSession> submitResponse(@RequestBody UserResponse userResponse) {
        System.out.println("📝 Processing response for session: " + userResponse.getSessionId());
        Optional<InterviewSession> sessionOpt = AskPattern.ask(
            sessionStorage,
            (ActorRef<Optional<InterviewSession>> replyTo) -> new SessionStorageActor.GetSession(userResponse.getSessionId(), replyTo),
            Duration.ofSeconds(5),
            actorSystem.scheduler()
        ).toCompletableFuture().join();
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        InterviewSession session = sessionOpt.get();
        int nextQuestionNumber = session.getQuestionCount() + 1;
        String followUp = aiQuestionService.generateFollowUp("User response: " + userResponse.getResponse(), nextQuestionNumber);
        session.setCurrentQuestion(followUp);
        session.incrementQuestionCount();
        session.addConversation(session.getCurrentQuestion(), userResponse.getResponse(), "", 0);
        sessionStorage.tell(new SessionStorageActor.StoreSession(session));
        System.out.println("✅ Response processed, next question: " + followUp);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/session/{sessionId}")
    public CompletionStage<ResponseEntity<InterviewSession>> getSession(@PathVariable String sessionId) {
        System.out.println("🔍 Retrieving session: " + sessionId);

        return AskPattern.ask(
            sessionStorage,
            (ActorRef<Optional<InterviewSession>> replyTo) -> new SessionStorageActor.GetSession(sessionId, replyTo),
            Duration.ofSeconds(5),
            actorSystem.scheduler()
        ).thenApply(sessionOpt -> {
            if (sessionOpt.isPresent()) {
                return ResponseEntity.ok(sessionOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @PostMapping("/end/{sessionId}")
    public CompletionStage<ResponseEntity<String>> endInterview(@PathVariable String sessionId) {
        System.out.println("🏁 Ending interview session: " + sessionId);

    sessionStorage.tell(new SessionStorageActor.EndSession(sessionId));
    return java.util.concurrent.CompletableFuture.completedFuture(ResponseEntity.ok("Interview session ended successfully."));
    }
}
