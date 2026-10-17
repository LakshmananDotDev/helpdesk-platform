package com.helpdesk.helpdeskplatform.repository;

import com.helpdesk.helpdeskplatform.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutBoxEvent, Long> {
    List<OutBoxEvent> findTop50ByPublishedAtIsNullOrderByIdAsc();

    boolean existsByEventId(String eventId);
}
