package com.interviewprep.models;

import java.time.LocalDateTime;

public class QuestionResponse {
    private final String question;
    private final String response;
    private final String feedback;
    private final int score;
    private final LocalDateTime timestamp;

    public QuestionResponse(String question, String response, String feedback, int score) {
        this.question = question;
        this.response = response;
        this.feedback = feedback;
        this.score = score;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getQuestion() { return question; }
    public String getResponse() { return response; }
    public String getFeedback() { return feedback; }
    public int getScore() { return score; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
