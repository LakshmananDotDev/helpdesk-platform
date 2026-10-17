package com.helpdesk.helpdeskplatform.messaging;

import com.helpdesk.helpdeskplatform.config.KafkaTopicConfig;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.event.TicketEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventLoggingConsumer {

    public final ObjectMapper objectMapper;
    public final TicketEventProcessor ticketEventProcessor;

    @KafkaListener(topics = KafkaTopicConfig.TICKET_EVENTS, groupId = "helpdesk-logger")
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("Processing attempt: partition={} offset={} key={}",
                record.partition(), record.offset(), record.key());

        TicketEvent event = objectMapper.readValue(record.value(), TicketEvent.class);
        ticketEventProcessor.process(event);
    }

}
