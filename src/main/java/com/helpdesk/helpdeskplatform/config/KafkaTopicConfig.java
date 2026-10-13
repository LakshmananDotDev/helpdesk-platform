package com.helpdesk.helpdeskplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TICKET_EVENTS = "ticket-events";
    public static final String TICKET_EVENTS_DLT = "ticket-events.DLT";

    @Bean
    public NewTopic ticketEventsTopic(){
        return TopicBuilder.name(TICKET_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ticketEventsDLTTopic(){
        return TopicBuilder.name(TICKET_EVENTS_DLT)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
