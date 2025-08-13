package com.interviewprep.services;

import akka.actor.typed.ActorSystem;
import com.interviewprep.actors.ClusterManager;
import com.interviewprep.models.InterviewSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InterviewService {

    @Autowired
    private ActorSystem<ClusterManager.Command> actorSystem;

    public void logInterviewMetrics(InterviewSession session) {
        System.out.println("📊 Interview Metrics for Session: " + session.getSessionId());
        System.out.println("   Job Title: " + session.getJobTitle());
        System.out.println("   Topic: " + session.getTopic());
        System.out.println("   Questions Asked: " + session.getQuestionCount());
        System.out.println("   Duration: " + java.time.Duration.between(session.getStartTime(), java.time.LocalDateTime.now()).toMinutes() + " minutes");
        System.out.println("   Conversation Items: " + session.getConversation().size());
    }
}
