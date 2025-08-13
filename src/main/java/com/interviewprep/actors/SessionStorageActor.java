package com.interviewprep.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.interviewprep.models.InterviewSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionStorageActor extends AbstractBehavior<SessionStorageActor.Command> {

    public interface Command {}

    public static final class StoreSession implements Command {
        public final InterviewSession session;

        public StoreSession(InterviewSession session) {
            this.session = session;
        }
    }

    public static final class GetSession implements Command {
        public final String sessionId;
        public final ActorRef<Optional<InterviewSession>> replyTo;

        public GetSession(String sessionId, ActorRef<Optional<InterviewSession>> replyTo) {
            this.sessionId = sessionId;
            this.replyTo = replyTo;
        }
    }

    public static final class UpdateCurrentQuestion implements Command {
        public final String sessionId;
        public final String question;

        public UpdateCurrentQuestion(String sessionId, String question) {
            this.sessionId = sessionId;
            this.question = question;
        }
    }

    public static final class EndSession implements Command {
        public final String sessionId;

        public EndSession(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    private final Map<String, InterviewSession> sessions = new HashMap<>();

    public static Behavior<Command> create() {
        return Behaviors.setup(SessionStorageActor::new);
    }

    private SessionStorageActor(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StoreSession.class, this::onStoreSession)
                .onMessage(GetSession.class, this::onGetSession)
                .onMessage(UpdateCurrentQuestion.class, this::onUpdateCurrentQuestion)
                .onMessage(EndSession.class, this::onEndSession)
                .build();
    }

    private Behavior<Command> onStoreSession(StoreSession command) {
        getContext().getLog().info("💾 Storing session: {}", command.session.getSessionId());
        sessions.put(command.session.getSessionId(), command.session);
        return this;
    }

    private Behavior<Command> onGetSession(GetSession command) {
        getContext().getLog().info("🔍 Retrieving session: {}", command.sessionId);
        InterviewSession session = sessions.get(command.sessionId);
        command.replyTo.tell(Optional.ofNullable(session));
        return this;
    }

    private Behavior<Command> onUpdateCurrentQuestion(UpdateCurrentQuestion command) {
        getContext().getLog().info("📝 Updating question for session: {}", command.sessionId);
        InterviewSession session = sessions.get(command.sessionId);
        if (session != null) {
            session.setCurrentQuestion(command.question);
            session.incrementQuestionCount();
        }
        return this;
    }

    private Behavior<Command> onEndSession(EndSession command) {
        getContext().getLog().info("🏁 Ending session: {}", command.sessionId);
        InterviewSession session = sessions.get(command.sessionId);
        if (session != null) {
            session.setActive(false);
        }
        return this;
    }
}
