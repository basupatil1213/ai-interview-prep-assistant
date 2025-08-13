package com.interviewprep.utils;

public class ActorSystemUtils {

    public static void logActorMessage(String actorName, String message) {
        System.out.println(String.format("[%s] %s: %s",
                java.time.LocalTime.now().toString(),
                actorName,
                message));
    }

    public static void logClusterEvent(String event) {
        System.out.println(String.format("🔗 [CLUSTER] %s", event));
    }
}