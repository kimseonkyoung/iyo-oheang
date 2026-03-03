package com.iyo.ohhaeng.app.pipeline;

import org.springframework.stereotype.Component;

/**
 * Hook: IdempotencyGuard 자리 (Normalize 이후, Parse 이전)
 * V0 — Noop. V1에서 (userId, requestId) TTL 체크로 대체 예정.
 */
@Component
public class IdempotencyStageNoop implements Stage {

    @Override
    public void process(SkillContext ctx) {
        // no-op
    }
}
