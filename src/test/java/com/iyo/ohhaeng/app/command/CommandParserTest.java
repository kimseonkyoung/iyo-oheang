package com.iyo.ohhaeng.app.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CommandParserTest {

    private final CommandParser parser = new CommandParser();

    // ── 정상 커맨드 ────────────────────────────────────────────────────

    @Test
    @DisplayName("내정보 파싱")
    void parse_myInfo() {
        Command cmd = parser.parse("내정보");
        assertThat(cmd.getType()).isEqualTo(CommandType.MY_INFO);
    }

    @Test
    @DisplayName("랭킹 파싱")
    void parse_ranking() {
        Command cmd = parser.parse("랭킹");
        assertThat(cmd.getType()).isEqualTo(CommandType.RANKING);
    }

    @Test
    @DisplayName("사냥 파싱")
    void parse_hunt() {
        Command cmd = parser.parse("사냥");
        assertThat(cmd.getType()).isEqualTo(CommandType.HUNT);
    }

    @Test
    @DisplayName("강화 파싱")
    void parse_enhance() {
        Command cmd = parser.parse("강화");
        assertThat(cmd.getType()).isEqualTo(CommandType.ENHANCE);
    }

    @Test
    @DisplayName("리롤 파싱")
    void parse_reroll() {
        Command cmd = parser.parse("리롤");
        assertThat(cmd.getType()).isEqualTo(CommandType.REROLL);
    }

    @Test
    @DisplayName("레이드 파싱")
    void parse_raid() {
        Command cmd = parser.parse("레이드");
        assertThat(cmd.getType()).isEqualTo(CommandType.RAID);
    }

    // ── 대결 (인자 추출) ───────────────────────────────────────────────

    @Test
    @DisplayName("대결 @닉네임 → DUEL, target 추출")
    void parse_duel_withAtSign() {
        Command cmd = parser.parse("대결 @홍길동");
        assertThat(cmd.getType()).isEqualTo(CommandType.DUEL);
        assertThat(cmd.getArgs().get("target")).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("대결 닉네임 → @ 없어도 target 추출")
    void parse_duel_withoutAtSign() {
        Command cmd = parser.parse("대결 홍길동");
        assertThat(cmd.getType()).isEqualTo(CommandType.DUEL);
        assertThat(cmd.getArgs().get("target")).isEqualTo("홍길동");
    }

    // ── 엣지 케이스 ───────────────────────────────────────────────────

    @Test
    @DisplayName("대결만 입력 (상대 없음) → UNKNOWN")
    void parse_duel_withoutTarget_returnsUnknown() {
        Command cmd = parser.parse("대결");
        assertThat(cmd.getType()).isEqualTo(CommandType.UNKNOWN);
    }

    @Test
    @DisplayName("대결 @ (닉네임 없음) → UNKNOWN")
    void parse_duel_emptyTarget_returnsUnknown() {
        Command cmd = parser.parse("대결 @");
        assertThat(cmd.getType()).isEqualTo(CommandType.UNKNOWN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("null·빈 문자열·공백 → UNKNOWN")
    void parse_blankInput_returnsUnknown(String input) {
        Command cmd = parser.parse(input);
        assertThat(cmd.getType()).isEqualTo(CommandType.UNKNOWN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"안녕", "hello", "1234", "???"})
    @DisplayName("인식 불가 입력 → UNKNOWN")
    void parse_unknownKeyword_returnsUnknown(String input) {
        Command cmd = parser.parse(input);
        assertThat(cmd.getType()).isEqualTo(CommandType.UNKNOWN);
    }
}
