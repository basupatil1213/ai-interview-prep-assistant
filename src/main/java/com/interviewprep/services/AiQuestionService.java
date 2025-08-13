package com.interviewprep.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiQuestionService {

    private final ChatClient chatClient;

    @Autowired
    public AiQuestionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateQuestion(String jobTitle, String topic, int questionNumber) {
        String promptText = String.format("Generate an interview question for a %s about %s. This is question #%d.", jobTitle, topic, questionNumber);
        return chatClient.prompt(new Prompt(promptText)).call().content();
    }

    public String generateFollowUp(String previousFeedback, int previousScore) {
        String promptText = "Generate only the next interview question, without any extra text or prefix. " +
            "Given the previous feedback: '" + previousFeedback + "' and score: " + previousScore + ".";
        return chatClient.prompt(new org.springframework.ai.chat.prompt.Prompt(promptText)).call().content();
    }

        // Generate overall interview feedback about user performance
        public String generateInterviewFeedback(String jobTitle, String topic, String conversationHistory) {
            String promptText = String.format(
                "You are an expert interviewer. Based on the following interview session for the job title '%s' and topic '%s', provide detailed feedback on how well the candidate performed. " +
                "Include strengths, weaknesses, and specific suggestions for improvement.\n\nSession:\n%s",
                jobTitle, topic, conversationHistory
            );
            return chatClient.prompt(new Prompt(promptText)).call().content();
        }
}
