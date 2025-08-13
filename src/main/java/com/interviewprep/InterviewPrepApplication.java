package com.interviewprep;

import akka.actor.typed.ActorSystem;
import com.interviewprep.actors.ClusterManager;
import com.interviewprep.config.AkkaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InterviewPrepApplication {

    @Autowired
    private AkkaConfiguration akkaConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(InterviewPrepApplication.class, args);
    }

    @Bean
    public CommandLineRunner startupRunner() {
        return args -> {
            System.out.println("🚀 AI Interview Prep Assistant Starting...");
            System.out.println("📡 Cluster nodes initializing...");
            System.out.println("🎯 Ready for mock interviews!");
            System.out.println("📋 Available endpoints:");
            System.out.println("   POST /api/interview/start - Start new interview");
            System.out.println("   POST /api/interview/respond - Submit response");
            System.out.println("   GET  /api/interview/session/{id} - Get session");
            System.out.println("   POST /api/interview/end/{id} - End interview");
            System.out.println("\n💡 Example curl command:");
            System.out.println("curl -X POST http://localhost:8080/api/interview/start \\");
            System.out.println("  -H \"Content-Type: application/json\" \\");
            System.out.println("  -d '{\"jobTitle\":\"Backend Engineer\",\"topic\":\"System Design\"}'");
        };
    }
}