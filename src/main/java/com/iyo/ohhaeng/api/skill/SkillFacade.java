package com.iyo.ohhaeng.api.skill;

import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import com.iyo.ohhaeng.app.pipeline.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SkillFacade {

    private final Pipeline pipeline;

    public SkillFacade(DecodeStage decodeStage, NormalizeStage normalizeStage, ParseStage parseStage) {
        this.pipeline = new Pipeline(List.of(decodeStage, normalizeStage, parseStage));
    }

    public SkillResponse process(String rawJson, String requestId) {
        SkillContext ctx = new SkillContext(rawJson, requestId);
        pipeline.run(ctx);

        log.info("[Skill] requestId={}, userId={}, command={}, hasCallback={}",
                requestId, ctx.userId(), ctx.command().type(), ctx.callbackUrl() != null);

        return switch (ctx.command().type()) {
            case MY_INFO -> SkillResponse.ofSimpleText("[내정보] 준비 중입니다.");
            case RANKING -> SkillResponse.ofSimpleText("[랭킹] 준비 중입니다.");
            case HUNT    -> SkillResponse.ofSimpleText("[사냥] 준비 중입니다.");
            case ENHANCE -> SkillResponse.ofSimpleText("[강화] 준비 중입니다.");
            case REROLL  -> SkillResponse.ofSimpleText("[리롤] 준비 중입니다.");
            case DUEL    -> SkillResponse.ofSimpleText("[대결] 준비 중입니다.");
            case RAID    -> SkillResponse.ofSimpleText("[레이드] 준비 중입니다.");
            case UNKNOWN -> SkillResponse.ofSimpleText("알 수 없는 명령어예요.");
        };
    }
}
