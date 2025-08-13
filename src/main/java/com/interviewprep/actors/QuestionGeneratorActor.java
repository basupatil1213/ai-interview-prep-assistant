package com.interviewprep.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestionGeneratorActor extends AbstractBehavior<QuestionGeneratorActor.Command> {

    public interface Command {}

    public static final class GenerateQuestion implements Command {
        public final String sessionId;
        public final String jobTitle;
        public final String topic;
        public final int questionNumber;
        public final ActorRef<InterviewManagerActor.Command> replyTo;

        public GenerateQuestion(String sessionId, String jobTitle, String topic,
                                int questionNumber, ActorRef<InterviewManagerActor.Command> replyTo) {
            this.sessionId = sessionId;
            this.jobTitle = jobTitle;
            this.topic = topic;
            this.questionNumber = questionNumber;
            this.replyTo = replyTo;
        }
    }

    public static final class GenerateFollowUpQuestion implements Command {
        public final String sessionId;
        public final String previousFeedback;
        public final int previousScore;
        public final ActorRef<InterviewManagerActor.Command> replyTo;

        public GenerateFollowUpQuestion(String sessionId, String previousFeedback,
                                        int previousScore, ActorRef<InterviewManagerActor.Command> replyTo) {
            this.sessionId = sessionId;
            this.previousFeedback = previousFeedback;
            this.previousScore = previousScore;
            this.replyTo = replyTo;
        }
    }

    private final Random random = new Random();

    public static Behavior<Command> create() {
        return Behaviors.setup(QuestionGeneratorActor::new);
    }

    private QuestionGeneratorActor(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GenerateQuestion.class, this::onGenerateQuestion)
                .onMessage(GenerateFollowUpQuestion.class, this::onGenerateFollowUpQuestion)
                .build();
    }

    private Behavior<Command> onGenerateQuestion(GenerateQuestion command) {
        getContext().getLog().info("🧠 Generating question #{} for {} - {}",
                command.questionNumber, command.jobTitle, command.topic);

        String question = generateContextualQuestion(command.jobTitle, command.topic, command.questionNumber);

        command.replyTo.tell(new InterviewManagerActor.QuestionGenerated(
                command.sessionId,
                question
        ));

        return this;
    }

    private Behavior<Command> onGenerateFollowUpQuestion(GenerateFollowUpQuestion command) {
        getContext().getLog().info("🎯 Generating follow-up question based on score: {}", command.previousScore);

        String question = generateFollowUpBasedOnScore(command.previousScore);

        command.replyTo.tell(new InterviewManagerActor.QuestionGenerated(
                command.sessionId,
                question
        ));

        return this;
    }

    private String generateContextualQuestion(String jobTitle, String topic, int questionNumber) {
        // Simulated LLM question generation based on job title and topic
        List<String> systemDesignQuestions = Arrays.asList(
                "Design a URL shortening service like bit.ly. How would you handle millions of requests?",
                "How would you design a chat application like WhatsApp to handle real-time messaging?",
                "Design a distributed cache system. What are the key considerations?",
                "How would you design a social media feed that scales to millions of users?",
                "Design a video streaming service like YouTube. Focus on the backend architecture."
        );

        List<String> backendQuestions = Arrays.asList(
                "Explain the difference between REST and GraphQL. When would you use each?",
                "How do you handle database transactions in a microservices architecture?",
                "What strategies would you use to optimize API performance?",
                "Describe your approach to implementing authentication and authorization.",
                "How would you design a system to handle payment processing securely?"
        );

        List<String> frontendQuestions = Arrays.asList(
                "How would you optimize the performance of a React application?",
                "Explain the virtual DOM and how it improves performance.",
                "What are the best practices for state management in large applications?",
                "How do you ensure accessibility in web applications?",
                "Describe your approach to implementing responsive design."
        );

        if (topic.toLowerCase().contains("system design")) {
            return systemDesignQuestions.get(random.nextInt(systemDesignQuestions.size()));
        } else if (jobTitle.toLowerCase().contains("backend")) {
            return backendQuestions.get(random.nextInt(backendQuestions.size()));
        } else if (jobTitle.toLowerCase().contains("frontend")) {
            return frontendQuestions.get(random.nextInt(frontendQuestions.size()));
        }

        return "Tell me about a challenging technical problem you've solved and your approach.";
    }

    private String generateFollowUpBasedOnScore(int previousScore) {
        if (previousScore >= 8) {
            return "Great answer! Let's dive deeper - how would you handle this at enterprise scale?";
        } else if (previousScore >= 6) {
            return "Good foundation. Can you elaborate on the trade-offs in your approach?";
        } else if (previousScore >= 4) {
            return "Let's try a different angle. What would be your first step in solving this problem?";
        } else {
            return "Let's start with fundamentals. Can you explain the basic concepts involved?";
        }
    }
}
