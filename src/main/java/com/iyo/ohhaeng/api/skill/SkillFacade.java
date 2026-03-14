package com.iyo.ohhaeng.api.skill;

import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import com.iyo.ohhaeng.app.pipeline.*;
import com.iyo.ohhaeng.app.usecase.DuelUseCase;
import com.iyo.ohhaeng.app.usecase.EnhanceUseCase;
import com.iyo.ohhaeng.app.usecase.GetMyInfoUseCase;
import com.iyo.ohhaeng.app.usecase.HuntUseCase;
import com.iyo.ohhaeng.app.usecase.RerollUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SkillFacade {

    private final Pipeline pipeline;
    private final GetMyInfoUseCase getMyInfoUseCase;
    private final HuntUseCase huntUseCase;
    private final EnhanceUseCase enhanceUseCase;
    private final RerollUseCase rerollUseCase;
    private final DuelUseCase duelUseCase;

    public SkillFacade(DecodeStage decodeStage, NormalizeStage normalizeStage,
                       IdempotencyStageNoop idempotencyStageNoop,
                       ParseStage parseStage,
                       RateLimitStageNoop rateLimitStageNoop,
                       GetMyInfoUseCase getMyInfoUseCase,
                       HuntUseCase huntUseCase,
                       EnhanceUseCase enhanceUseCase,
                       RerollUseCase rerollUseCase,
                       DuelUseCase duelUseCase) {
        this.pipeline = new Pipeline(List.of(
                decodeStage, normalizeStage, idempotencyStageNoop, parseStage, rateLimitStageNoop
        ));
        this.getMyInfoUseCase = getMyInfoUseCase;
        this.huntUseCase = huntUseCase;
        this.enhanceUseCase = enhanceUseCase;
        this.rerollUseCase = rerollUseCase;
        this.duelUseCase = duelUseCase;
    }

    public SkillResponse process(String rawJson, String requestId) {
        SkillContext ctx = new SkillContext(rawJson, requestId);
        pipeline.run(ctx);

        if (ctx.isFailed()) {
            log.warn("[Skill] pipeline failed: requestId={}, reason={}", requestId, ctx.failReason());
            return SkillResponse.ofSimpleText("요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.");
        }

        log.info("[Skill] requestId={}, userId={}, command={}, hasCallback={}",
                requestId, ctx.userId(), ctx.command().type(), ctx.callbackUrl() != null);

        return switch (ctx.command().type()) {
            case MY_INFO -> SkillResponse.ofSimpleText(getMyInfoUseCase.execute(ctx.userId()));
            case RANKING -> SkillResponse.ofSimpleText("[랭킹] 준비 중입니다.");
            case HUNT    -> SkillResponse.ofSimpleText(huntUseCase.execute(ctx.userId()));
            case ENHANCE -> SkillResponse.ofSimpleText(enhanceUseCase.execute(ctx.userId()));
            case REROLL  -> SkillResponse.ofSimpleText(rerollUseCase.execute(ctx.userId()));
            case DUEL    -> SkillResponse.ofSimpleText(
                    duelUseCase.execute(ctx.userId(), ctx.command().args().get("target")));
            case RAID    -> SkillResponse.ofSimpleText("[레이드] 준비 중입니다.");
            case UNKNOWN -> SkillResponse.ofSimpleText("알 수 없는 명령어예요.");
        };
    }
}
