package com.iyo.ohhaeng.domain.battle;

import lombok.Getter;

@Getter
public class RoundLog {

    private final int round;
    private final int aBase;
    private final double aMult;
    private final int aGain;
    private final int bBase;
    private final double bMult;
    private final int bGain;

    public RoundLog(int round, int aBase, double aMult, int aGain,
                    int bBase, double bMult, int bGain) {
        this.round = round;
        this.aBase = aBase;
        this.aMult = aMult;
        this.aGain = aGain;
        this.bBase = bBase;
        this.bMult = bMult;
        this.bGain = bGain;
    }
}
