package com.interviewprep.config;

import akka.actor.typed.ActorSystem;
import com.interviewprep.actors.ClusterManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfiguration {

    @Bean
    public ActorSystem<ClusterManager.Command> actorSystem() {
        Config config = ConfigFactory.load();
        return ActorSystem.create(ClusterManager.create(), "InterviewCluster", config);
    }
}
