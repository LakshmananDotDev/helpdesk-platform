package com.helpdesk.helpdeskplatform.messaging;

import com.helpdesk.helpdeskplatform.config.KafkaTopicConfig;
import com.helpdesk.helpdeskplatform.config.SlaProperties;
import com.helpdesk.helpdeskplatform.event.TicketEvent;
import com.helpdesk.helpdeskplatform.event.TicketEventType;
import com.helpdesk.helpdeskplatform.service.SlaTimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlaEventConsumer {

    public static final String GROUP_ID = "sla-tracker";

    private final ObjectMapper objectMapper;
    private final SlaProperties slaProperties;
    private final SlaTimerService slaTimerService;

    @KafkaListener(topics = KafkaTopicConfig.TICKET_EVENTS, groupId = GROUP_ID)
    public void onMessage(ConsumerRecord<String, String> record){
        TicketEvent event = objectMapper.readValue(record.value(), TicketEvent.class);

        if(TicketEventType.TICKET_CREATED.name().equals(event.eventType())){
            Instant start = event.occurredAt() != null ? event.occurredAt() : Instant.now();
            long deadline = start.plusSeconds(slaProperties.responseSecondsFor(event.priority())).getEpochSecond();
            slaTimerService.start(event.ticketId(), deadline);
            log.info("SLA timer started for ticket {} priority {} deadline {}",
                    event.ticketId(), event.priority(), deadline);
        }else if(!"OPEN".equals(event.status())){
            slaTimerService.cancel(event.ticketId());
            log.info("SLA timer cancelled for ticket {}",event.ticketId());
        }
    }

}
