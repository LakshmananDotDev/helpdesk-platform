package com.helpdesk.helpdeskplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SlaTimerService {

    private static final String DEADLINES_KEY = "sla:deadlines";

    private final StringRedisTemplate redis;

    public void start(Long ticketId, long deadlineEpochSeconds) {
        redis.opsForZSet().add(DEADLINES_KEY, String.valueOf(ticketId), deadlineEpochSeconds);
    }

    public void cancel(Long ticketId) {
        redis.opsForZSet().remove(DEADLINES_KEY, String.valueOf(ticketId));
    }

    public Set<String> findOverdue(long nowEpochSeconds, int limit) {
        Set<String> overdue = redis.opsForZSet().rangeByScore(DEADLINES_KEY, 0, nowEpochSeconds, 0, limit);
        return overdue == null ? Set.of() : overdue;
    }
}