package com.interviewprep.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Random;

public class EvaluationActor extends AbstractBehavior<EvaluationActor.Command> {

    public interface Command {}

    public static final class EvaluateResponse implements Command {
        public final String sessionId;
        public final String response;
        public final ActorRef<InterviewManagerActor.Command> replyTo;

        public EvaluateResponse(String sessionId, String response,
                                ActorRef<InterviewManagerActor.Command> replyTo) {
            this.sessionId = sessionId;
            this.response = response;
            this.replyTo = replyTo;
        }
    }

    private final Random random = new Random();

    public static Behavior<Command> create() {
        return Behaviors.setup(EvaluationActor::new);
    }

    private EvaluationActor(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(EvaluateResponse.class, this::onEvaluateResponse)
                .build();
    }

    private Behavior<Command> onEvaluateResponse(EvaluateResponse command) {
        getContext().getLog().info("🔍 Evaluating response for session: {}", command.sessionId);

        // Simulated LLM evaluation
        EvaluationResult result = evaluateResponse(command.response);

        command.replyTo.tell(new InterviewManagerActor.EvaluationComplete(
                command.sessionId,
                result.feedback,
                result.score
        ));

        return this;
    }

    private EvaluationResult evaluateResponse(String response) {
        // Simulated evaluation logic
        int responseLength = response.length();
        int score;
        String feedback;

        if (responseLength > 200) {
            score = 7 + random.nextInt(3); // 7-9
            feedback = "Excellent detailed response! You covered multiple aspects and showed deep understanding.";
        } else if (responseLength > 100) {
            score = 5 + random.nextInt(3); // 5-7
            feedback = "Good response with solid fundamentals. Consider adding more specific examples.";
        } else if (responseLength > 50) {
            score = 3 + random.nextInt(3); // 3-5
            feedback = "Basic understanding shown. Try to elaborate more on your reasoning and approach.";
        } else {
            score = 1 + random.nextInt(3); // 1-3
            feedback = "Brief response. Please provide more detail to demonstrate your knowledge.";
        }

        return new EvaluationResult(feedback, score);
    }

    private static class EvaluationResult {
        final String feedback;
        final int score;

        EvaluationResult(String feedback, int score) {
            this.feedback = feedback;
            this.score = score;
        }
    }
}
