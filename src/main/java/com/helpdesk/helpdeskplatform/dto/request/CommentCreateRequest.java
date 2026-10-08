package com.helpdesk.helpdeskplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank(message = "Comment body is required")
    @Size(max = 5000, message = "Comment must be under 5000 characters")
    private String body;
}