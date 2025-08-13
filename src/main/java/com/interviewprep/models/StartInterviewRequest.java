package com.interviewprep.models;

public class StartInterviewRequest {
    private String jobTitle;
    private String topic;

    // Constructors
    public StartInterviewRequest() {}
    public StartInterviewRequest(String jobTitle, String topic) {
        this.jobTitle = jobTitle;
        this.topic = topic;
    }

    // Getters and setters
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
