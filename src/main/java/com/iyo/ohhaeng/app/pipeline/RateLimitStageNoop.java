package com.iyo.ohhaeng.app.pipeline;

import org.springframework.stereotype.Component;

/**
 * Hook: RateLimit 자리 (Parse 이후, UseCase 이전)
 * V0 — Noop. V1에서 Semaphore/토큰 버킷으로 대체 예정.
 */
@Component
public class RateLimitStageNoop implements Stage {

    @Override
    public void process(SkillContext ctx) {
        // no-op
    }
}
