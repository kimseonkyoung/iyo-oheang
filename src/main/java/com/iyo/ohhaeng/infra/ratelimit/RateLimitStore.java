package com.iyo.ohhaeng.infra.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * V0 인메모리 슬라이딩 윈도우 레이트 리밋 저장소.
 *
 * 유저별로 최근 요청 시각을 큐에 유지하고,
 * 윈도우 밖의 항목을 제거한 뒤 한도 초과 여부를 판단한다.
 */
@Component
public class RateLimitStore {

    private final ConcurrentHashMap<String, ArrayDeque<Instant>> store = new ConcurrentHashMap<>();

    /**
     * 요청을 소비한다.
     * 윈도우 내 요청 수가 한도 미만이면 기록 후 true, 초과면 false.
     */
    public boolean tryConsume(String userId, int limit, long windowSeconds) {
        Instant now = Instant.now();
        ArrayDeque<Instant> deque = store.computeIfAbsent(userId, k -> new ArrayDeque<>());

        synchronized (deque) {
            Instant cutoff = now.minusSeconds(windowSeconds);
            while (!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
                deque.pollFirst();
            }
            if (deque.size() >= limit) return false;
            deque.addLast(now);
            return true;
        }
    }
}
