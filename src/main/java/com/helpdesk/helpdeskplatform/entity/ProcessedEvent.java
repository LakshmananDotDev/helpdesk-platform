package com.helpdesk.helpdeskplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "processed_events",
uniqueConstraints = @UniqueConstraint(
        name = "uk_processed_consumer_event",
        columnNames = {"consumer_name", "event_id"}
))
@Data
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_name", nullable = false)
    private String consumer_name;

    @Column(name = "event_id", nullable = false)
    private String event_id;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
