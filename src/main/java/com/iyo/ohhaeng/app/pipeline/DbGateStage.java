package com.iyo.ohhaeng.app.pipeline;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

/**
 * 파이프라인 Hook: DB 동시 접근 수 제한 (Backpressure).
 *
 * Semaphore(permits) — HikariCP 커넥션 풀 크기와 맞춤.
 * tryAcquire() 실패 시 즉시 거절 (NOWAIT).
 * 허가 반납은 SkillFacade의 finally 블록에서 수행.
 */
@Component
public class DbGateStage implements Stage {

    private final Semaphore semaphore;

    public DbGateStage(@Value("${skill.db-gate.permits:10}") int permits) {
        this.semaphore = new Semaphore(permits);
    }

    @Override
    public void process(SkillContext ctx) {
        if (!semaphore.tryAcquire()) {
            ctx.fail("DB_GATE_FULL");
            return;
        }
        ctx.markDbPermitAcquired();
    }

    public void release() {
        semaphore.release();
    }
}
