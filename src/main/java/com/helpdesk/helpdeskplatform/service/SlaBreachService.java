package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.dto.response.TicketResponse;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.event.TicketEventType;
import com.helpdesk.helpdeskplatform.repository.OutboxEventRepository;
import com.helpdesk.helpdeskplatform.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlaBreachService {

    private final TicketRepository ticketRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxService outboxService;

    @Transactional
    public boolean recordBreachIfStillOpen(Long ticketId){
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if(ticket == null || ticket.getStatus() != Ticket.Status.OPEN){
            return false;
        }

        String eventId = "sla-breach-" + ticketId;
        if(outboxEventRepository.existsByEventId(eventId)){
            return false;
        }

        outboxService.recordTicketEvent(TicketEventType.SLA_BREACHED, ticket,eventId);
        return true;
    }

}
