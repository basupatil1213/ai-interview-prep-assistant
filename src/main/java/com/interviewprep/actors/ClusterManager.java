package com.interviewprep.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;

public class ClusterManager extends AbstractBehavior<ClusterManager.Command> {

    public interface Command {}

    public static final class StartCluster implements Command {
        public final String seedNodeAddress;
        public StartCluster(String seedNodeAddress) {
            this.seedNodeAddress = seedNodeAddress;
        }
    }

    private final ActorRef<InterviewManagerActor.Command> interviewManager;
    private final ActorRef<QuestionGeneratorActor.Command> questionGenerator;
    private final ActorRef<EvaluationActor.Command> evaluationActor;
    private final ActorRef<SessionStorageActor.Command> sessionStorage;

    public static Behavior<Command> create() {
        return Behaviors.setup(ClusterManager::new);
    }

    private ClusterManager(ActorContext<Command> context) {
        super(context);

        // Initialize cluster
        Cluster cluster = Cluster.get(context.getSystem());
        cluster.manager().tell(Join.create(cluster.selfMember().address()));

        // Spawn child actors
        this.sessionStorage = context.spawn(SessionStorageActor.create(), "session-storage");
        this.questionGenerator = context.spawn(QuestionGeneratorActor.create(), "question-generator");
        this.evaluationActor = context.spawn(EvaluationActor.create(), "evaluation-actor");
        this.interviewManager = context.spawn(
                InterviewManagerActor.create(questionGenerator, evaluationActor, sessionStorage),
                "interview-manager"
        );

        context.getLog().info("🎯 Cluster Manager started with all child actors");
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCluster.class, this::onStartCluster)
                .build();
    }

    private Behavior<Command> onStartCluster(StartCluster command) {
        getContext().getLog().info("Starting cluster with seed node: {}", command.seedNodeAddress);
        return this;
    }

    public ActorRef<InterviewManagerActor.Command> getInterviewManager() {
        return interviewManager;
    }
}