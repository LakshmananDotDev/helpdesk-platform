package com.helpdesk.helpdeskplatform.repository;

import com.helpdesk.helpdeskplatform.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByOrganizationId(Long organizationId, Pageable pageable);
    Page<Ticket> findByOrganizationIdAndStatus(Long organizationId, Ticket.Status status, Pageable pageable);
    Page<Ticket> findByOrganizationIdAndPriority(Long organizationId, Ticket.Priority priority, Pageable pageable);
    Page<Ticket> findByOrganizationIdAndStatusAndPriority(Long organizationId, Ticket.Status status, Ticket.Priority priority, Pageable pageable);
}
