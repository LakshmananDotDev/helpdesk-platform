package com.helpdesk.helpdeskplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "outbox_events",
indexes = @Index(name = "idx_outbox_published_at", columnList = "published_at"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutBoxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String eventKey;

    @Column(nullable = false)
    private Long organizationId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = Instant.now();
    }

}
