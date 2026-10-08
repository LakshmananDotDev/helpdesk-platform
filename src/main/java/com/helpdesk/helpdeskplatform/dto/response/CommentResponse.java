package com.helpdesk.helpdeskplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long ticketId;
    private Long authorId;
    private String authorEmail;
    private String body;
    private Instant createdAt;
}