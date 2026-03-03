package com.iyo.ohhaeng.app.pipeline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizeStageTest {

    private final NormalizeStage stage = new NormalizeStage();

    @Test
    @DisplayName("앞의 / 제거")
    void normalize_stripsLeadingSlash() {
        assertThat(normalize("/사냥")).isEqualTo("사냥");
    }

    @Test
    @DisplayName("앞뒤 공백 제거")
    void normalize_stripsEdgeSpaces() {
        assertThat(normalize("  /사냥  ")).isEqualTo("사냥");
    }

    @Test
    @DisplayName("연속 공백 → 단일 공백")
    void normalize_collapsesSpaces() {
        assertThat(normalize("/대결  @홍길동")).isEqualTo("대결 @홍길동");
    }

    @Test
    @DisplayName("슬래시 없는 입력은 그대로")
    void normalize_noSlash_unchanged() {
        assertThat(normalize("사냥")).isEqualTo("사냥");
    }

    @Test
    @DisplayName("null → 빈 문자열")
    void normalize_null_returnsEmpty() {
        assertThat(normalize(null)).isEqualTo("");
    }

    @Test
    @DisplayName("줄바꿈 포함 입력 → 단일 공백으로 수렴")
    void normalize_newline_collapsedToSpace() {
        assertThat(normalize("/대결\n@홍길동")).isEqualTo("대결 @홍길동");
    }

    @Test
    @DisplayName("탭 포함 입력 → 단일 공백으로 수렴")
    void normalize_tab_collapsedToSpace() {
        assertThat(normalize("/대결\t@홍길동")).isEqualTo("대결 @홍길동");
    }

    // ──────────────────────────────────────────────────────────────────

    private String normalize(String utterance) {
        SkillContext ctx = new SkillContext("{}", null);
        ctx.utterance(utterance);
        stage.process(ctx);
        return ctx.normalizedUtterance();
    }
}
