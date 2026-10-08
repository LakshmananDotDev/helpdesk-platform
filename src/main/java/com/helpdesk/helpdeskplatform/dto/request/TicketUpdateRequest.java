package com.helpdesk.helpdeskplatform.dto.request;

import com.helpdesk.helpdeskplatform.entity.Ticket;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketUpdateRequest {

    @Size(max = 255, message = "Subject must be under 255 characters")
    private String subject;

    private String description;

    private Ticket.Status status;

    private Ticket.Priority priority;

    @NotNull(message = "Version is required")
    private Long version;
}