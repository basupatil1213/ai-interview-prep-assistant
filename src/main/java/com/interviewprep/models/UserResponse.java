package com.interviewprep.models;

public class UserResponse {
    private String sessionId;
    private String response;

    // Constructors
    public UserResponse() {}
    public UserResponse(String sessionId, String response) {
        this.sessionId = sessionId;
        this.response = response;
    }

    // Getters and setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
