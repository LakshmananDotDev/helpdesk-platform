package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.entity.OutBoxEvent;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.event.TicketEvent;
import com.helpdesk.helpdeskplatform.event.TicketEventType;
import com.helpdesk.helpdeskplatform.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void recordTicketEvent(TicketEventType type, Ticket ticket){
        String eventId = UUID.randomUUID().toString();

        TicketEvent event = new TicketEvent(
                eventId,
                type.name(),
                ticket.getId(),
                ticket.getOrganization().getId(),
                ticket.getSubject(),
                ticket.getStatus().name(),
                ticket.getPriority().name(),
                ticket.getAssignedTo() == null ? null : ticket.getAssignedTo().getId(),
                ticket.getVersion(),
                Instant.now()
        );

        OutBoxEvent row = new OutBoxEvent();
        row.setEventId(eventId);
        row.setEventType(type.name());
        row.setEventKey(String.valueOf(ticket.getId()));
        row.setOrganizationId(event.organizationId());
        row.setPayload(objectMapper.writeValueAsString(event));

        outboxEventRepository.save(row);
    }

}
