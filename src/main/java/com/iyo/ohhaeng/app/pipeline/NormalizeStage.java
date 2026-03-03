package com.iyo.ohhaeng.app.pipeline;

import org.springframework.stereotype.Component;

@Component
public class NormalizeStage implements Stage {

    @Override
    public void process(SkillContext ctx) {
        String raw = ctx.utterance();

        if (raw == null) {
            ctx.normalizedUtterance("");
            return;
        }

        String normalized = raw.strip();           // 앞뒤 공백 제거
        normalized = collapseSpaces(normalized);   // 연속 공백 → 단일 공백
        normalized = stripLeadingSlash(normalized); // 앞의 / 제거

        ctx.normalizedUtterance(normalized);
    }

    private String collapseSpaces(String s) {
        return s.replaceAll("\\s+", " ");
    }

    // "/사냥" → "사냥",  "/대결 @홍길동" → "대결 @홍길동"
    // 슬래시 없는 입력은 그대로
    private String stripLeadingSlash(String s) {
        return s.startsWith("/") ? s.substring(1) : s;
    }
}
