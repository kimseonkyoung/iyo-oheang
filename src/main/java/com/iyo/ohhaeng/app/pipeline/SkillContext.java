package com.iyo.ohhaeng.app.pipeline;

import com.iyo.ohhaeng.app.command.Command;

/**
 * 파이프라인을 흐르는 요청 컨텍스트.
 * 각 Stage가 자신의 책임 필드를 채워 넣는다.
 *
 * DecodeStage  → utterance, userId, callbackUrl
 * NormalizeStage → normalizedUtterance
 * ParseStage   → command
 */
public class SkillContext {

    // ── 진입 시 세팅 (Controller → Facade) ──────────────────────────
    private final String rawJson;
    private final String requestId;

    // ── DecodeStage가 채움 ───────────────────────────────────────────
    private String utterance;
    private String userId;
    private String callbackUrl;

    // ── NormalizeStage가 채움 ────────────────────────────────────────
    private String normalizedUtterance;

    // ── ParseStage가 채움 ────────────────────────────────────────────
    private Command command;

    public SkillContext(String rawJson, String requestId) {
        this.rawJson = rawJson;
        this.requestId = requestId;
    }

    // ── getters ──────────────────────────────────────────────────────

    public String rawJson() { return rawJson; }
    public String requestId() { return requestId; }

    public String utterance() { return utterance; }
    public String userId() { return userId; }
    public String callbackUrl() { return callbackUrl; }

    public String normalizedUtterance() { return normalizedUtterance; }

    public Command command() { return command; }

    // ── setters (각 Stage에서만 호출) ────────────────────────────────

    public void utterance(String utterance) { this.utterance = utterance; }
    public void userId(String userId) { this.userId = userId; }
    public void callbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public void normalizedUtterance(String normalizedUtterance) {
        this.normalizedUtterance = normalizedUtterance;
    }

    public void command(Command command) { this.command = command; }
}
