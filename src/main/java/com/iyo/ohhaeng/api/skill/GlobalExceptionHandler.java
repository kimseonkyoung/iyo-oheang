package com.iyo.ohhaeng.api.skill;

import com.iyo.ohhaeng.api.skill.dto.SkillResponse;
import com.iyo.ohhaeng.app.exception.ResourceLockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 동시 요청으로 row 락 충돌 시
     * FOR UPDATE NOWAIT → ORA-00054 → ResourceLockedException
     */
    @ExceptionHandler(ResourceLockedException.class)
    public SkillResponse handleResourceLocked(ResourceLockedException e) {
        return SkillResponse.ofSimpleText(e.getMessage());
    }

    /**
     * 예상치 못한 서버 오류 fallback
     * 카카오 스킬은 HTTP 200 + SkillResponse를 기대하므로 예외도 200으로 반환
     */
    @ExceptionHandler(Exception.class)
    public SkillResponse handleException(Exception e) {
        return SkillResponse.ofSimpleText("오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
