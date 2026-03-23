package com.iyo.ohhaeng.app.pipeline;

import com.iyo.ohhaeng.app.command.CommandType;
import com.iyo.ohhaeng.infra.idem.IdempotencyKey;
import com.iyo.ohhaeng.infra.idem.IdempotencyStore;
import com.iyo.ohhaeng.infra.time.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 파이프라인 Hook: 중복 요청 차단.
 *
 * 1. requestId 있음 → Strong Idem (TTL 60s)
 * 2. requestId 없음 → Alt-key fallback (utteranceHash + timeBucket, TTL 5s)
 * 3. 쓰기 커맨드 → Debounce (TTL 1s) 추가 적용
 *
 * 히트 시 ctx.fail("IDEM_HIT") 후 리턴.
 */
@RequiredArgsConstructor
@Component
public class IdempotencyStage implements Stage {

    private final IdempotencyStore store;
    private final ClockHolder clockHolder;

    @Override
    public void process(SkillContext ctx) {
        String userId = ctx.userId();

        // 1. 멱등성 체크 (Strong Idem or Alt-key)
        String idemKey;
        long idemTtl;
        if (ctx.requestId() != null && !ctx.requestId().isBlank()) {
            idemKey = IdempotencyKey.req(userId, ctx.requestId());
            idemTtl = 60;
        } else {
            long timeBucket = clockHolder.now().toEpochMilli() / 2000;
            idemKey = IdempotencyKey.alt(userId, ctx.normalizedUtterance().hashCode(), timeBucket);
            idemTtl = 5;
        }

        if (!store.trySet(idemKey, idemTtl)) {
            ctx.fail("IDEM_HIT");
            return;
        }

        // 2. 디바운스 체크 (쓰기 커맨드만)
        if (isWriteCommand(ctx.command().getType())) {
            String debounceKey = IdempotencyKey.cmd(userId, ctx.command().getType());
            if (!store.trySet(debounceKey, 1)) {
                ctx.fail("IDEM_HIT");
            }
        }
    }

    private boolean isWriteCommand(CommandType type) {
        return switch (type) {
            case HUNT, ENHANCE, REROLL, DUEL -> true;
            default -> false;
        };
    }
}
