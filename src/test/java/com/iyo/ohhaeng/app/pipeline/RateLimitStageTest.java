package com.iyo.ohhaeng.app.pipeline;

import com.iyo.ohhaeng.app.command.Command;
import com.iyo.ohhaeng.app.command.CommandType;
import com.iyo.ohhaeng.infra.ratelimit.RateLimitStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitStageTest {

    private RateLimitStage stage;

    @BeforeEach
    void setUp() {
        stage = new RateLimitStage(new RateLimitStore(), 3, 60);  // 60초 내 3회 한도
    }

    @Test
    @DisplayName("한도 미만 요청 → 통과")
    void underLimit_passes() {
        for (int i = 0; i < 3; i++) {
            SkillContext ctx = context("user-001");
            stage.process(ctx);
            assertThat(ctx.isFailed()).isFalse();
        }
    }

    @Test
    @DisplayName("한도 초과 요청 → RATE_LIMIT 차단")
    void overLimit_blocked() {
        for (int i = 0; i < 3; i++) {
            stage.process(context("user-001"));
        }

        SkillContext overflow = context("user-001");
        stage.process(overflow);

        assertThat(overflow.isFailed()).isTrue();
        assertThat(overflow.failReason()).isEqualTo("RATE_LIMIT");
    }

    @Test
    @DisplayName("다른 유저는 독립적으로 카운트")
    void differentUsers_independentCounts() {
        for (int i = 0; i < 3; i++) {
            stage.process(context("user-001"));
        }

        SkillContext ctx = context("user-002");
        stage.process(ctx);

        assertThat(ctx.isFailed()).isFalse();
    }

    @Test
    @DisplayName("첫 요청은 항상 통과")
    void firstRequest_alwaysPasses() {
        SkillContext ctx = context("user-new");
        stage.process(ctx);
        assertThat(ctx.isFailed()).isFalse();
    }

    // ── helpers ───────────────────────────────────────────────────────

    private SkillContext context(String userId) {
        SkillContext ctx = new SkillContext("{}", null);
        ctx.userId(userId);
        ctx.normalizedUtterance("사냥");
        ctx.command(Command.of(CommandType.HUNT));
        return ctx;
    }
}
