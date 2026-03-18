package com.iyo.ohhaeng.infra.idem;

import com.iyo.ohhaeng.app.command.CommandType;

public class IdempotencyKey {

    private IdempotencyKey() {}

    /** 카카오 requestId 기반 강한 멱등성 키 (TTL 60s) */
    public static String req(String userId, String requestId) {
        return "idem:req:" + userId + ":" + requestId;
    }

    /** CommandType 기반 디바운스 키 (TTL 1s) */
    public static String cmd(String userId, CommandType commandType) {
        return "idem:cmd:" + userId + ":" + commandType.name();
    }

    /** requestId 없을 때 fallback 키 (TTL 5s) */
    public static String alt(String userId, int utteranceHash, long timeBucket) {
        return "idem:alt:" + userId + ":" + utteranceHash + ":" + timeBucket;
    }
}
