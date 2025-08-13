package com.interviewprep.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.interviewprep.models.InterviewSession;

public class InterviewManagerActor extends AbstractBehavior<InterviewManagerActor.Command> {

    public interface Command {}

    public static final class StartInterview implements Command {
        public final String jobTitle;
        public final String topic;
        public final ActorRef<InterviewSession> replyTo;

        public StartInterview(String jobTitle, String topic, ActorRef<InterviewSession> replyTo) {
            this.jobTitle = jobTitle;
            this.topic = topic;
            this.replyTo = replyTo;
        }
    }

    public static final class ProcessResponse implements Command {
        public final String sessionId;
        public final String response;
        public final ActorRef<String> replyTo;

        public ProcessResponse(String sessionId, String response, ActorRef<String> replyTo) {
            this.sessionId = sessionId;
            this.response = response;
            this.replyTo = replyTo;
        }
    }

    public static final class QuestionGenerated implements Command {
        public final String sessionId;
        public final String question;

        public QuestionGenerated(String sessionId, String question) {
            this.sessionId = sessionId;
            this.question = question;
        }
    }

    public static final class EvaluationComplete implements Command {
        public final String sessionId;
        public final String feedback;
        public final int score;

        public EvaluationComplete(String sessionId, String feedback, int score) {
            this.sessionId = sessionId;
            this.feedback = feedback;
            this.score = score;
        }
    }

    private final ActorRef<QuestionGeneratorActor.Command> questionGenerator;
    private final ActorRef<EvaluationActor.Command> evaluationActor;
    private final ActorRef<SessionStorageActor.Command> sessionStorage;

    public static Behavior<Command> create(
            ActorRef<QuestionGeneratorActor.Command> questionGenerator,
            ActorRef<EvaluationActor.Command> evaluationActor,
            ActorRef<SessionStorageActor.Command> sessionStorage) {
        return Behaviors.setup(context ->
                new InterviewManagerActor(context, questionGenerator, evaluationActor, sessionStorage));
    }

    private InterviewManagerActor(
            ActorContext<Command> context,
            ActorRef<QuestionGeneratorActor.Command> questionGenerator,
            ActorRef<EvaluationActor.Command> evaluationActor,
            ActorRef<SessionStorageActor.Command> sessionStorage) {
        super(context);
        this.questionGenerator = questionGenerator;
        this.evaluationActor = evaluationActor;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartInterview.class, this::onStartInterview)
                .onMessage(ProcessResponse.class, this::onProcessResponse)
                .onMessage(QuestionGenerated.class, this::onQuestionGenerated)
                .onMessage(EvaluationComplete.class, this::onEvaluationComplete)
                .build();
    }

    private Behavior<Command> onStartInterview(StartInterview command) {
        getContext().getLog().info("🎬 Starting interview for {} - {}", command.jobTitle, command.topic);

        // Create new session
        InterviewSession session = new InterviewSession(command.jobTitle, command.topic);

        // TELL pattern: Store session
        sessionStorage.tell(new SessionStorageActor.StoreSession(session));

        // ASK pattern: Generate first question
        questionGenerator.tell(new QuestionGeneratorActor.GenerateQuestion(
                session.getSessionId(),
                command.jobTitle,
                command.topic,
                1,
                getContext().getSelf()
        ));

        // Reply with session info
        command.replyTo.tell(session);

        return this;
    }

    private Behavior<Command> onProcessResponse(ProcessResponse command) {
        getContext().getLog().info("🗣️ Processing response for session: {}", command.sessionId);

        // FORWARD pattern: Forward to evaluation actor
        evaluationActor.tell(new EvaluationActor.EvaluateResponse(
                command.sessionId,
                command.response,
                getContext().getSelf()
        ));

        return this;
    }

    private Behavior<Command> onQuestionGenerated(QuestionGenerated command) {
        getContext().getLog().info("❓ Question generated for session: {}", command.sessionId);

        // TELL pattern: Update session with new question
        sessionStorage.tell(new SessionStorageActor.UpdateCurrentQuestion(
                command.sessionId,
                command.question
        ));

        return this;
    }

    private Behavior<Command> onEvaluationComplete(EvaluationComplete command) {
        getContext().getLog().info("✅ Evaluation complete for session: {} (Score: {})",
                command.sessionId, command.score);

        // Generate next question based on performance
        questionGenerator.tell(new QuestionGeneratorActor.GenerateFollowUpQuestion(
                command.sessionId,
                command.feedback,
                command.score,
                getContext().getSelf()
        ));

        return this;
    }
}
