package com.helpdesk.helpdeskplatform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketAssignRequest {

    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;

    @NotNull(message = "Version is required")
    private Long version;

}
