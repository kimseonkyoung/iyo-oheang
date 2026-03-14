package com.iyo.ohhaeng.domain.battle;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Battle {

    private Long battleId;
    private String roomId;
    private String aUserId;
    private String bUserId;
    private int aSum;
    private int bSum;
    private String outcome;
    private String roundLogJson;
    private Instant createdAt;

    private Battle() {}

    public static Battle of(String roomId, String aUserId, String bUserId,
                            int aSum, int bSum, String outcome,
                            String roundLogJson, Instant createdAt) {
        Battle b = new Battle();
        b.roomId = roomId;
        b.aUserId = aUserId;
        b.bUserId = bUserId;
        b.aSum = aSum;
        b.bSum = bSum;
        b.outcome = outcome;
        b.roundLogJson = roundLogJson;
        b.createdAt = createdAt;
        return b;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }
}
