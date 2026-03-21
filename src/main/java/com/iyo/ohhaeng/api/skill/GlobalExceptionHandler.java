package com.iyo.ohhaeng.api.skill;

import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import com.iyo.ohhaeng.app.exception.ResourceLockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 동시 요청으로 row 락 충돌 시
     * FOR UPDATE NOWAIT → ORA-00054 → ResourceLockedException
     */
    @ExceptionHandler(ResourceLockedException.class)
    public SkillResponse handleResourceLocked(ResourceLockedException e) {
        log.warn("[Exception] ResourceLocked: {}", e.getMessage());
        return SkillResponse.ofSimpleText(e.getMessage());
    }

    /**
     * 사용자 입력 오류 (잘못된 인자, 존재하지 않는 대상 등)
     * 예: DuelUseCase — 상대를 찾을 수 없습니다
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public SkillResponse handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[Exception] IllegalArgument: {}", e.getMessage());
        return SkillResponse.ofSimpleText(e.getMessage());
    }

    /**
     * 예상치 못한 서버 오류 fallback
     * 카카오 스킬은 HTTP 200 + SkillResponse를 기대하므로 예외도 200으로 반환
     */
    @ExceptionHandler(Exception.class)
    public SkillResponse handleException(Exception e) {
        log.error("[Exception] Unexpected error", e);
        return SkillResponse.ofSimpleText("오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
