package com.iyo.ohhaeng.app.command;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommandParser {

    private static final Map<String, CommandType> KEYWORD_MAP = Map.of(
            "내정보", CommandType.MY_INFO,
            "랭킹",  CommandType.RANKING,
            "사냥",  CommandType.HUNT,
            "강화",  CommandType.ENHANCE,
            "리롤",  CommandType.REROLL,
            "대결",  CommandType.DUEL,
            "레이드", CommandType.RAID,
            "이름",  CommandType.RENAME
    );

    public Command parse(String normalizedUtterance) {
        if (normalizedUtterance == null || normalizedUtterance.isBlank()) {
            return Command.unknown();
        }

        // 첫 번째 단어 = 키워드, 나머지 = 인자
        String[] parts = normalizedUtterance.split(" ", 2);
        String keyword = parts[0];

        CommandType type = KEYWORD_MAP.get(keyword);
        if (type == null) {
            return Command.unknown();
        }

        return switch (type) {
            case DUEL   -> parseDuel(parts);
            case RENAME -> parseRename(parts);
            default     -> Command.of(type);
        };
    }

    // "이름 김선경" → Command(RENAME, {name: "김선경"})
    private Command parseRename(String[] parts) {
        if (parts.length < 2 || parts[1].isBlank()) {
            return Command.unknown();
        }
        String name = parts[1].strip();
        if (name.length() > 12) {
            return Command.unknown();
        }
        return Command.of(CommandType.RENAME, Map.of("name", name));
    }

    // "대결 @홍길동" → Command(DUEL, {target: "홍길동"})
    // 상대 없이 "대결"만 입력 → UNKNOWN
    private Command parseDuel(String[] parts) {
        if (parts.length < 2 || parts[1].isBlank()) {
            return Command.unknown();
        }

        String raw = parts[1].strip();
        String target = raw.startsWith("@") ? raw.substring(1) : raw;

        if (target.isBlank()) {
            return Command.unknown();
        }

        return Command.of(CommandType.DUEL, Map.of("target", target));
    }
}
