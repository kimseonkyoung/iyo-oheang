package com.iyo.ohhaeng.domain.battle;

import lombok.Getter;

import java.util.List;

@Getter
public class BattleResult {

    private final List<RoundLog> rounds;
    private final int aSum;
    private final int bSum;
    private final String outcome; // "WIN_A" / "WIN_B" / "DRAW"

    public BattleResult(List<RoundLog> rounds, int aSum, int bSum, String outcome) {
        this.rounds = rounds;
        this.aSum = aSum;
        this.bSum = bSum;
        this.outcome = outcome;
    }
}
