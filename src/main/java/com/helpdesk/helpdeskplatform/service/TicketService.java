package com.helpdesk.helpdeskplatform.service;

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
import com.helpdesk.helpdeskplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public TicketResponse createTicket(Long organizationId, Long raisedById, TicketCreateRequest request){
        Organization organization = organizationRepository.findById(raisedById)
                .orElseThrow(() -> new ResourceNotFoundException("Organization Not Found"));
        User user = userRepository.findById(raisedById)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        Ticket ticket = new Ticket();
        ticket.setOrganization(organization);
        ticket.setRaisedBy(user);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long organizationId, Long ticketId){
        Ticket ticket = ticketRepository.findByIdAndOrganizationId(ticketId,organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));
        return ticketMapper.toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> listTickets(Long organizationId, Ticket.Status status, Ticket.Priority priority, Pageable pageable){
        Page<Ticket> page;
        if(status != null && priority != null){
            page = ticketRepository.findByOrganizationIdAndStatusAndPriority(organizationId, status, priority, pageable);
        }
        else if(status != null){
            page = ticketRepository.findByOrganizationIdAndStatus(organizationId, status, pageable);
        }
        else if(priority != null){
            page = ticketRepository.findByOrganizationIdAndPriority(organizationId, priority, pageable);
        }
        else{
            page = ticketRepository.findByOrganizationId(organizationId, pageable);
        }
        return page.map(ticketMapper::toResponse);
    }

    @Transactional
    public TicketResponse updateTicket(Long organizationId, Long ticketId, TicketUpdateRequest request){
        Ticket ticket = ticketRepository.findByIdAndOrganizationId(ticketId,organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        if(!ticket.getVersion().equals(request.getVersion())){
            throw new ObjectOptimisticLockingFailureException(Ticket.class, ticketId);
        }

        if(request.getSubject() != null){
            if(request.getSubject().isBlank()){
                throw new IllegalArgumentException("Subject must not be blank");
            }
            ticket.setSubject(request.getSubject());
        }
        if(request.getDescription() != null){
            ticket.setDescription(request.getDescription());
        }
        if(request.getPriority() != null){
            ticket.setPriority(request.getPriority());
        }
        if(request.getStatus() != null){
            ticket.setStatus(request.getStatus());
        }
        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }
}
