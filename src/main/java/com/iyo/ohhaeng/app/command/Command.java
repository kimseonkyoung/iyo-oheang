package com.iyo.ohhaeng.app.command;

import java.util.Map;

/**
 * utterance 파싱 결과물.
 * args 키는 CommandType별로 다르다:
 *   DUEL  → "target" (상대 userId 또는 닉네임)
 *   그 외 → 비어 있음
 */
public record Command(CommandType type, Map<String, String> args) {

    public static Command of(CommandType type) {
        return new Command(type, Map.of());
    }

    public static Command of(CommandType type, Map<String, String> args) {
        return new Command(type, Map.copyOf(args));
    }

    public static Command unknown() {
        return new Command(CommandType.UNKNOWN, Map.of());
    }
}
