package com.iyo.ohhaeng.app.pipeline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DbGateStageTest {

    @Test
    @DisplayName("허가 획득 성공 → dbPermitAcquired true")
    void acquire_success_marksPermitAcquired() {
        DbGateStage stage = new DbGateStage(1);
        SkillContext ctx = context();

        stage.process(ctx);

        assertThat(ctx.isFailed()).isFalse();
        assertThat(ctx.isDbPermitAcquired()).isTrue();
    }

    @Test
    @DisplayName("허가 소진 → DB_GATE_FULL 차단")
    void acquire_full_blocked() {
        DbGateStage stage = new DbGateStage(1);

        SkillContext first = context();
        stage.process(first);       // 허가 1개 소진

        SkillContext second = context();
        stage.process(second);      // 허가 없음

        assertThat(first.isFailed()).isFalse();
        assertThat(second.isFailed()).isTrue();
        assertThat(second.failReason()).isEqualTo("DB_GATE_FULL");
        assertThat(second.isDbPermitAcquired()).isFalse();
    }

    @Test
    @DisplayName("허가 반납 후 재요청 → 통과")
    void release_thenAcquire_passes() {
        DbGateStage stage = new DbGateStage(1);

        SkillContext first = context();
        stage.process(first);
        stage.release();            // 허가 반납

        SkillContext second = context();
        stage.process(second);

        assertThat(second.isFailed()).isFalse();
        assertThat(second.isDbPermitAcquired()).isTrue();
    }

    @Test
    @DisplayName("허가 수만큼 동시 요청 → 모두 통과")
    void acquire_upToLimit_allPass() {
        DbGateStage stage = new DbGateStage(3);

        for (int i = 0; i < 3; i++) {
            SkillContext ctx = context();
            stage.process(ctx);
            assertThat(ctx.isFailed()).isFalse();
        }
    }

    // ── helpers ───────────────────────────────────────────────────────

    private SkillContext context() {
        return new SkillContext("{}", null);
    }
}
