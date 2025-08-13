package com.interviewprep.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InterviewSession {
    private final String sessionId;
    private final String jobTitle;
    private final String topic;
    private final LocalDateTime startTime;
    private final List<QuestionResponse> conversation;
    private String currentQuestion;
    private boolean isActive;
    private int questionCount;

    @JsonCreator
    public InterviewSession(@JsonProperty("jobTitle") String jobTitle,
                            @JsonProperty("topic") String topic) {
        this.sessionId = UUID.randomUUID().toString();
        this.jobTitle = jobTitle;
        this.topic = topic;
        this.startTime = LocalDateTime.now();
        this.conversation = new ArrayList<>();
        this.isActive = true;
        this.questionCount = 0;
    }

    // Getters and setters
    public String getSessionId() { return sessionId; }
    public String getJobTitle() { return jobTitle; }
    public String getTopic() { return topic; }
    public LocalDateTime getStartTime() { return startTime; }
    public List<QuestionResponse> getConversation() { return conversation; }
    public String getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(String currentQuestion) { this.currentQuestion = currentQuestion; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public int getQuestionCount() { return questionCount; }
    public void incrementQuestionCount() { this.questionCount++; }

    public void addConversation(String question, String response, String feedback, int score) {
        conversation.add(new QuestionResponse(question, response, feedback, score));
    }
}