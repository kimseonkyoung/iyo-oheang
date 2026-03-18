package com.iyo.ohhaeng.infra.idem;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * V0 인메모리 TTL 키-값 저장소.
 * Redis SET NX EX 의미론을 ConcurrentHashMap + CAS로 모사한다.
 *
 * V1에서 Redis로 교체 예정.
 */
@Component
public class IdempotencyStore {

    private final ConcurrentHashMap<String, Instant> store = new ConcurrentHashMap<>();

    /**
     * 키가 없거나 만료된 경우에만 세팅 후 true 반환 (SET NX EX 의미론).
     * 유효한 키가 이미 존재하면 false 반환.
     */
    public boolean trySet(String key, long ttlSeconds) {
        Instant expiry = Instant.now().plusSeconds(ttlSeconds);

        Instant prev = store.putIfAbsent(key, expiry);
        if (prev == null) return true;  // 새로 삽입 성공

        // 이미 존재하지만 만료됐으면 CAS 교체 시도
        if (prev.isBefore(Instant.now())) {
            return store.replace(key, prev, expiry);
        }
        return false;  // 유효한 키 존재 → 중복 요청
    }
}
