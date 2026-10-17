package com.helpdesk.helpdeskplatform.messaging;

import com.helpdesk.helpdeskplatform.event.TicketEvent;
import com.helpdesk.helpdeskplatform.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketEventProcessor {

    public static final String CONSUMER_NAME = "helpdesk-logger";

    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void process(TicketEvent event) {
        int inserted = processedEventRepository.insertIfAbsent(CONSUMER_NAME, event.eventId());
        if(inserted == 0){
            log.info("Duplicate event ignored eventId={} type={}", event.eventId());
            return;
        }

        log.info("Handled {} for ticket {} (org {}) eventId={}",
                event.eventType(), event.ticketId(), event.organizationId(), event.eventId());
    }

}
