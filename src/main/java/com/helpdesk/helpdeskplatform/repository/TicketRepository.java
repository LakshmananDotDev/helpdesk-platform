package com.helpdesk.helpdeskplatform.repository;

import com.helpdesk.helpdeskplatform.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    Optional<Ticket> findByIdAndOrganizationId(Long id, Long organizationId);
}
