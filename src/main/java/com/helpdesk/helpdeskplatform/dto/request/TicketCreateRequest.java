package com.helpdesk.helpdeskplatform.dto.request;

import com.helpdesk.helpdeskplatform.entity.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketCreateRequest {

    @NotBlank(message = "Subject is required")
    @Size(max = 255, message = "Subject must be under 255 characters")
    private String subject;

    private String description;

    @NotNull(message = "Priority is required")
    private Ticket.Priority priority;
}
