package com.helpdesk.helpdeskplatform.messaging;

import com.helpdesk.helpdeskplatform.config.KafkaTopicConfig;
import com.helpdesk.helpdeskplatform.entity.OutBoxEvent;
import com.helpdesk.helpdeskplatform.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void publishPending() {
        List<OutBoxEvent> pending = outboxEventRepository.findTop50ByPublishedAtIsNullOrderByIdAsc();

        for(OutBoxEvent row : pending){
            try{
                SendResult<String, String> result = kafkaTemplate
                        .send(KafkaTopicConfig.TICKET_EVENTS, row.getEventKey(), row.getPayload())
                        .get(10, TimeUnit.SECONDS);

                row.setPublishedAt(Instant.now());
                outboxEventRepository.save(row);

                log.info("Published outbox event id={} type={} key={} partition={} offset={}",
                        row.getId(), row.getEventType(), row.getEventKey(),
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e){
                log.warn("Could not publish outbox event id={}, will retry: {}", row.getId(), e.toString());
                return;
            }
        }
    }

}
