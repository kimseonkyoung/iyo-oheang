package com.iyo.ohhaeng.app.pipeline;

import com.iyo.ohhaeng.infra.ratelimit.RateLimitStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 파이프라인 Hook: 유저별 요청 횟수 제한 (Sliding Window).
 *
 * windowSeconds 내 limit 회 초과 시 즉시 거절.
 * DbGateStage 앞에 위치해 허가 획득 전에 걸러낸다.
 */
@Component
public class RateLimitStage implements Stage {

    private final RateLimitStore store;
    private final int limit;
    private final long windowSeconds;

    public RateLimitStage(RateLimitStore store,
                          @Value("${skill.rate-limit.limit:30}") int limit,
                          @Value("${skill.rate-limit.window-seconds:60}") long windowSeconds) {
        this.store = store;
        this.limit = limit;
        this.windowSeconds = windowSeconds;
    }

    @Override
    public void process(SkillContext ctx) {
        if (!store.tryConsume(ctx.userId(), limit, windowSeconds)) {
            ctx.fail("RATE_LIMIT");
        }
    }
}
