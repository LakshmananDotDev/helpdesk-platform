package com.helpdesk.helpdeskplatform.event;

import java.time.Instant;

public record TicketEvent(
        String eventId,
        String eventType,
        Long ticketId,
        Long organizationId,
        String subject,
        String status,
        String priority,
        Long assignedToId,
        Long version,
        Instant occuredAt
){

}
