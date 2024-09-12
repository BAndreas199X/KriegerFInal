package com.andreas.webapp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Configuration for creating the KafkaTopics
 * @return
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("AndreasB4KriegerUpdates")
                .partitions(10)
                .replicas(3)
                .compact()
                .build();
    }
    
    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("AndreasB4KriegerDeletion")
                .partitions(10)
                .replicas(3)
                .compact()
                .build();
    }
}
