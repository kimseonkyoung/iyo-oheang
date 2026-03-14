package com.iyo.ohhaeng.infra.db.oracle;

import com.iyo.ohhaeng.app.exception.ResourceLockedException;

import java.util.function.Supplier;

public class LockingSupport {

    private LockingSupport() {}

    public static <T> T execute(Supplier<T> action) {
        try {
            return action.get();
        } catch (Exception e) {
            if (isOraLockException(e)) {
                throw new ResourceLockedException("다른 요청이 처리 중입니다. 잠시 후 재시도해 주세요.");
            }
            throw e;
        }
    }

    private static boolean isOraLockException(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            String msg = cause.getMessage();
            if (msg != null && (msg.contains("ORA-00054") || msg.contains("resource busy"))) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
