package com.helpdesk.helpdeskplatform.repository;

import com.helpdesk.helpdeskplatform.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    @Modifying
    @Query(value = "INSERT INTO processed_events (consumer_name, event_id, processed_at) "
            + "VALUES (:consumer, :eventId, now()) "
            + "ON CONFLICT (consumer_name, event_id) DO NOTHING",
            nativeQuery = true)
    int insertIfAbsent(@Param("consumer") String consumer, @Param("eventId") String eventId);
}