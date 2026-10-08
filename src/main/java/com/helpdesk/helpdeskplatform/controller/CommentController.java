package com.helpdesk.helpdeskplatform.controller;

import com.helpdesk.helpdeskplatform.dto.request.CommentCreateRequest;
import com.helpdesk.helpdeskplatform.dto.response.CommentResponse;
import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import com.helpdesk.helpdeskplatform.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> add(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse created = commentService.addComment(
                user.organizationId(), user.userId(), ticketId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.listComments(user.organizationId(), ticketId));
    }
}