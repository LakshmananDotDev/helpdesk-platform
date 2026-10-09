package com.helpdesk.helpdeskplatform.controller;

import com.helpdesk.helpdeskplatform.dto.request.TicketAssignRequest;
import com.helpdesk.helpdeskplatform.dto.request.TicketCreateRequest;
import com.helpdesk.helpdeskplatform.dto.request.TicketUpdateRequest;
import com.helpdesk.helpdeskplatform.dto.response.TicketResponse;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import com.helpdesk.helpdeskplatform.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody TicketCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(user, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicket(user, id));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> list(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) Ticket.Status status,
            @RequestParam(required = false) Ticket.Priority priority,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ticketService.listTickets(user, status, priority, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<TicketResponse> update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateRequest request) {
        return ResponseEntity.ok(ticketService.updateTicket(user, id, request));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<TicketResponse> assign(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @Valid @RequestBody TicketAssignRequest request) {
        return ResponseEntity.ok(ticketService.assignTicket(user, id, request));
    }
}