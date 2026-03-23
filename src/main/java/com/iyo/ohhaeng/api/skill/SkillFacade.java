package com.iyo.ohhaeng.api.skill;

import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import com.iyo.ohhaeng.app.pipeline.DbGateStage;
import com.iyo.ohhaeng.app.pipeline.DecodeStage;
import com.iyo.ohhaeng.app.pipeline.IdempotencyStage;
import com.iyo.ohhaeng.app.pipeline.NormalizeStage;
import com.iyo.ohhaeng.app.pipeline.ParseStage;
import com.iyo.ohhaeng.app.pipeline.Pipeline;
import com.iyo.ohhaeng.app.pipeline.RateLimitStage;
import com.iyo.ohhaeng.app.pipeline.SkillContext;
import com.iyo.ohhaeng.app.usecase.DuelUseCase;
import com.iyo.ohhaeng.app.usecase.EnhanceUseCase;
import com.iyo.ohhaeng.app.usecase.GetMyInfoUseCase;
import com.iyo.ohhaeng.app.usecase.GetRankingUseCase;
import com.iyo.ohhaeng.app.usecase.HuntUseCase;
import com.iyo.ohhaeng.app.usecase.RerollUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SkillFacade {

    private final Pipeline pipeline;
    private final DbGateStage dbGateStage;
    private final GetMyInfoUseCase getMyInfoUseCase;
    private final GetRankingUseCase getRankingUseCase;
    private final HuntUseCase huntUseCase;
    private final EnhanceUseCase enhanceUseCase;
    private final RerollUseCase rerollUseCase;
    private final DuelUseCase duelUseCase;

    public SkillFacade(DecodeStage decodeStage, NormalizeStage normalizeStage,
                       ParseStage parseStage,
                       IdempotencyStage idempotencyStage,
                       RateLimitStage rateLimitStage,
                       DbGateStage dbGateStage,
                       GetMyInfoUseCase getMyInfoUseCase,
                       GetRankingUseCase getRankingUseCase,
                       HuntUseCase huntUseCase,
                       EnhanceUseCase enhanceUseCase,
                       RerollUseCase rerollUseCase,
                       DuelUseCase duelUseCase) {
        this.pipeline = new Pipeline(List.of(
                decodeStage, normalizeStage, parseStage, idempotencyStage, rateLimitStage, dbGateStage
        ));
        this.dbGateStage = dbGateStage;
        this.getMyInfoUseCase = getMyInfoUseCase;
        this.getRankingUseCase = getRankingUseCase;
        this.huntUseCase = huntUseCase;
        this.enhanceUseCase = enhanceUseCase;
        this.rerollUseCase = rerollUseCase;
        this.duelUseCase = duelUseCase;
    }

    public SkillResponse process(String rawJson, String requestId) {
        SkillContext ctx = new SkillContext(rawJson, requestId);

        try {
            pipeline.run(ctx);

            if (ctx.isFailed()) {
                log.warn("[Skill] pipeline failed: requestId={}, reason={}", requestId, ctx.failReason());
                return switch (ctx.failReason()) {
                    case "IDEM_HIT"      -> SkillResponse.ofSimpleText("이미 처리된 요청입니다.");
                    case "RATE_LIMIT"    -> SkillResponse.ofSimpleText("요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.");
                    case "DB_GATE_FULL"  -> SkillResponse.ofSimpleText("잠시 후 다시 시도해 주세요.");
                    default              -> SkillResponse.ofSimpleText("요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.");
                };
            }

            log.info("[Skill] requestId={}, userId={}, command={}, hasCallback={}",
                    requestId, ctx.userId(), ctx.command().getType(), ctx.callbackUrl() != null);

            return switch (ctx.command().getType()) {
                case MY_INFO -> SkillResponse.ofSimpleText(getMyInfoUseCase.execute(ctx.userId()));
                case RANKING -> SkillResponse.ofSimpleText(getRankingUseCase.execute());
                case HUNT    -> SkillResponse.ofSimpleText(huntUseCase.execute(ctx.userId()));
                case ENHANCE -> SkillResponse.ofSimpleText(enhanceUseCase.execute(ctx.userId()));
                case REROLL  -> SkillResponse.ofSimpleText(rerollUseCase.execute(ctx.userId()));
                case DUEL    -> SkillResponse.ofSimpleText(
                        duelUseCase.execute(ctx.userId(), ctx.command().getArgs().get("target")));
                case RAID    -> SkillResponse.ofSimpleText("[레이드] 준비 중입니다.");
                case UNKNOWN -> SkillResponse.ofSimpleText("알 수 없는 명령어예요.");
            };
        } finally {
            if (ctx.isDbPermitAcquired()) {
                dbGateStage.release();
            }
        }
    }
}
