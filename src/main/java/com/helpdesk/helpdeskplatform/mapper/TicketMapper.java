package com.helpdesk.helpdeskplatform.mapper;

import com.helpdesk.helpdeskplatform.dto.response.TicketResponse;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponse toResponse(Ticket ticket) {
        User raisedBy = ticket.getRaisedBy();
        User assignedTo = ticket.getAssignedTo();

        return new TicketResponse(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                raisedBy != null ? raisedBy.getId() : null,
                raisedBy != null ? raisedBy.getEmail() : null,
                assignedTo != null ? assignedTo.getId() : null,
                assignedTo != null ? assignedTo.getEmail() : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getVersion()
        );
    }
}
