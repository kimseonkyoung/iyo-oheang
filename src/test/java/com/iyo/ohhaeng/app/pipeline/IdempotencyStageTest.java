package com.iyo.ohhaeng.app.pipeline;

import com.iyo.ohhaeng.app.command.Command;
import com.iyo.ohhaeng.app.command.CommandType;
import com.iyo.ohhaeng.infra.idem.IdempotencyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotencyStageTest {

    private IdempotencyStage stage;

    @BeforeEach
    void setUp() {
        stage = new IdempotencyStage(new IdempotencyStore());
    }

    // ── Strong Idem ───────────────────────────────────────────────────

    @Test
    @DisplayName("같은 requestId 첫 번째 요청 → 통과")
    void strongIdem_firstRequest_passes() {
        SkillContext ctx = context("user-001", "req-abc", CommandType.HUNT);
        stage.process(ctx);
        assertThat(ctx.isFailed()).isFalse();
    }

    @Test
    @DisplayName("같은 requestId 두 번째 요청 → IDEM_HIT 차단")
    void strongIdem_duplicateRequest_blocked() {
        SkillContext ctx1 = context("user-001", "req-abc", CommandType.HUNT);
        SkillContext ctx2 = context("user-001", "req-abc", CommandType.HUNT);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isTrue();
        assertThat(ctx2.failReason()).isEqualTo("IDEM_HIT");
    }

    @Test
    @DisplayName("다른 requestId, 다른 커맨드 → 각각 통과 (Debounce는 커맨드 단위)")
    void strongIdem_differentRequestIds_differentCommands_bothPass() {
        SkillContext ctx1 = context("user-001", "req-001", CommandType.HUNT);
        SkillContext ctx2 = context("user-001", "req-002", CommandType.ENHANCE);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isFalse();
    }

    @Test
    @DisplayName("다른 requestId, 같은 쓰기 커맨드 1초 내 → Debounce 차단")
    void strongIdem_differentRequestIds_sameWriteCommand_debounced() {
        SkillContext ctx1 = context("user-001", "req-001", CommandType.HUNT);
        SkillContext ctx2 = context("user-001", "req-002", CommandType.HUNT);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isTrue();
        assertThat(ctx2.failReason()).isEqualTo("IDEM_HIT");
    }

    // ── Alt-key ───────────────────────────────────────────────────────

    @Test
    @DisplayName("requestId 없이 같은 발화 즉시 재요청 → IDEM_HIT 차단")
    void altKey_duplicateUtterance_blocked() {
        SkillContext ctx1 = contextNoRequestId("user-001", "사냥", CommandType.HUNT);
        SkillContext ctx2 = contextNoRequestId("user-001", "사냥", CommandType.HUNT);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isTrue();
        assertThat(ctx2.failReason()).isEqualTo("IDEM_HIT");
    }

    @Test
    @DisplayName("requestId 없이 다른 발화 → 각각 통과")
    void altKey_differentUtterances_bothPass() {
        SkillContext ctx1 = contextNoRequestId("user-001", "사냥", CommandType.HUNT);
        SkillContext ctx2 = contextNoRequestId("user-001", "강화", CommandType.ENHANCE);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isFalse();
    }

    // ── Debounce ──────────────────────────────────────────────────────

    @Test
    @DisplayName("쓰기 커맨드 연속 요청 → 두 번째 IDEM_HIT 차단")
    void debounce_writeCommand_secondBlocked() {
        SkillContext ctx1 = context("user-001", "req-001", CommandType.HUNT);
        SkillContext ctx2 = context("user-001", "req-002", CommandType.HUNT);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isTrue();
        assertThat(ctx2.failReason()).isEqualTo("IDEM_HIT");
    }

    @Test
    @DisplayName("조회 커맨드(MY_INFO)는 Debounce 미적용 → 연속 요청 모두 통과")
    void debounce_readCommand_notApplied() {
        SkillContext ctx1 = context("user-001", "req-001", CommandType.MY_INFO);
        SkillContext ctx2 = context("user-001", "req-002", CommandType.MY_INFO);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isFalse();
    }

    @Test
    @DisplayName("다른 유저의 같은 커맨드 → 서로 영향 없이 각각 통과")
    void debounce_differentUsers_independent() {
        SkillContext ctx1 = context("user-001", "req-001", CommandType.HUNT);
        SkillContext ctx2 = context("user-002", "req-002", CommandType.HUNT);

        stage.process(ctx1);
        stage.process(ctx2);

        assertThat(ctx1.isFailed()).isFalse();
        assertThat(ctx2.isFailed()).isFalse();
    }

    // ── helpers ───────────────────────────────────────────────────────

    private SkillContext context(String userId, String requestId, CommandType type) {
        SkillContext ctx = new SkillContext("{}", requestId);
        ctx.userId(userId);
        ctx.normalizedUtterance(type.name().toLowerCase());
        ctx.command(Command.of(type));
        return ctx;
    }

    private SkillContext contextNoRequestId(String userId, String utterance, CommandType type) {
        SkillContext ctx = new SkillContext("{}", null);
        ctx.userId(userId);
        ctx.normalizedUtterance(utterance);
        ctx.command(Command.of(type));
        return ctx;
    }
}
