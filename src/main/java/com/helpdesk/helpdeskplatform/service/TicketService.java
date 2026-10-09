package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.dto.request.TicketAssignRequest;
import com.helpdesk.helpdeskplatform.dto.request.TicketCreateRequest;
import com.helpdesk.helpdeskplatform.dto.request.TicketUpdateRequest;
import com.helpdesk.helpdeskplatform.dto.response.TicketResponse;
import com.helpdesk.helpdeskplatform.entity.Organization;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.entity.User;
import com.helpdesk.helpdeskplatform.exception.ResourceNotFoundException;
import com.helpdesk.helpdeskplatform.mapper.TicketMapper;
import com.helpdesk.helpdeskplatform.repository.OrganizationRepository;
import com.helpdesk.helpdeskplatform.repository.TicketRepository;
import com.helpdesk.helpdeskplatform.repository.TicketSpecifications;
import com.helpdesk.helpdeskplatform.repository.UserRepository;
import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponse createTicket(AuthenticatedUser actor, TicketCreateRequest request) {
        Organization organization = organizationRepository.findById(actor.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + actor.organizationId()));
        User raisedBy = userRepository.findById(actor.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actor.userId()));

        Ticket ticket = new Ticket();
        ticket.setOrganization(organization);
        ticket.setRaisedBy(raisedBy);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(AuthenticatedUser actor, Long ticketId) {
        return ticketMapper.toResponse(loadVisibleTicket(actor, ticketId));
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listTickets(AuthenticatedUser actor,
                                            Ticket.Status status,
                                            Ticket.Priority priority,
                                            Pageable pageable) {
        Specification<Ticket> spec = TicketSpecifications.inOrganization(actor.organizationId());
        if (actor.isCustomer()) {
            spec = spec.and(TicketSpecifications.raisedBy(actor.userId()));
        }
        if (status != null) {
            spec = spec.and(TicketSpecifications.hasStatus(status));
        }
        if (priority != null) {
            spec = spec.and(TicketSpecifications.hasPriority(priority));
        }
        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toResponse);
    }

    @Transactional
    public TicketResponse updateTicket(AuthenticatedUser actor, Long ticketId, TicketUpdateRequest request) {
        Ticket ticket = loadVisibleTicket(actor, ticketId);
        checkVersion(ticket, request.getVersion());

        if (request.getSubject() != null) {
            if (request.getSubject().isBlank()) {
                throw new IllegalArgumentException("Subject must not be blank");
            }
            ticket.setSubject(request.getSubject());
        }
        if (request.getDescription() != null) ticket.setDescription(request.getDescription());
        if (request.getStatus() != null) ticket.setStatus(request.getStatus());
        if (request.getPriority() != null) ticket.setPriority(request.getPriority());

        return ticketMapper.toResponse(ticketRepository.saveAndFlush(ticket));
    }

    @Transactional
    public TicketResponse assignTicket(AuthenticatedUser actor, Long ticketId, TicketAssignRequest request) {
        Ticket ticket = loadVisibleTicket(actor, ticketId);
        checkVersion(ticket, request.getVersion());

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getAssigneeId()));
        if (!assignee.getOrganization().getId().equals(actor.organizationId())) {
            throw new ResourceNotFoundException("User not found: " + request.getAssigneeId());
        }
        if (assignee.getRole() == User.Role.CUSTOMER) {
            throw new IllegalArgumentException("Tickets can only be assigned to agents or admins");
        }

        ticket.setAssignedTo(assignee);
        return ticketMapper.toResponse(ticketRepository.saveAndFlush(ticket));
    }

    public Ticket loadVisibleTicket(AuthenticatedUser actor, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndOrganizationId(ticketId, actor.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        if (actor.isCustomer() && !ticket.getRaisedBy().getId().equals(actor.userId())) {
            throw new ResourceNotFoundException("Ticket not found: " + ticketId);
        }
        return ticket;
    }

    private void checkVersion(Ticket ticket, Long expectedVersion) {
        if (!ticket.getVersion().equals(expectedVersion)) {
            throw new ObjectOptimisticLockingFailureException(Ticket.class, ticket.getId());
        }
    }
}