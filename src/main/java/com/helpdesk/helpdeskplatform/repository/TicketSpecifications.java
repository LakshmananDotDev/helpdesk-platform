package com.helpdesk.helpdeskplatform.repository;

import com.helpdesk.helpdeskplatform.entity.Ticket;
import org.springframework.data.jpa.domain.Specification;

public final class TicketSpecifications {

    private TicketSpecifications(){}

    public static Specification<Ticket> inOrganization(Long organizationId){
        return (root, query, cb) -> cb.equal(root.get("organization").get("id"), organizationId);
    }

    public static Specification<Ticket> hasStatus(Ticket.Status status){
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Ticket> hasPriority(Ticket.Priority priority){
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    public static Specification<Ticket> raisedBy(Long userId){
        return (root, query, cb) -> cb.equal(root.get("raisedBy").get("id"), userId);
    }

}
