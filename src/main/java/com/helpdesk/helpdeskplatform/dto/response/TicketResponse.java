package com.helpdesk.helpdeskplatform.dto.response;

import com.helpdesk.helpdeskplatform.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String subject;
    private String description;
    private Ticket.Status status;
    private Ticket.Priority priority;
    private Long raisedById;
    private String raisedByEmail;
    private Long assignedToId;
    private String assignedToEmail;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}
