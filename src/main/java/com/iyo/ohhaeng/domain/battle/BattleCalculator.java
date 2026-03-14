package com.iyo.ohhaeng.domain.battle;

import com.iyo.ohhaeng.domain.weapon.ElementType;
import com.iyo.ohhaeng.domain.weapon.Weapon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BattleCalculator {

    private static final int ROUNDS = 5;

    public BattleResult calculate(Weapon aWeapon, Weapon bWeapon) {
        List<RoundLog> rounds = new ArrayList<>();
        int aSum = 0;
        int bSum = 0;

        for (int round = 1; round <= ROUNDS; round++) {
            int aBase = aWeapon.getEnhanceLevel() * 2 + ThreadLocalRandom.current().nextInt(1, 12);
            int bBase = bWeapon.getEnhanceLevel() * 2 + ThreadLocalRandom.current().nextInt(1, 12);
            double aMult = multiplier(aWeapon.getElementType(), bWeapon.getElementType());
            double bMult = multiplier(bWeapon.getElementType(), aWeapon.getElementType());
            int aGain = (int) (aBase * aMult);
            int bGain = (int) (bBase * bMult);
            aSum += aGain;
            bSum += bGain;
            rounds.add(new RoundLog(round, aBase, aMult, aGain, bBase, bMult, bGain));
        }

        String outcome = aSum > bSum ? "WIN_A" : bSum > aSum ? "WIN_B" : "DRAW";
        return new BattleResult(List.copyOf(rounds), aSum, bSum, outcome);
    }

    private double multiplier(ElementType a, ElementType b) {
        if (a.beats(b)) return 1.2;
        if (b.beats(a)) return 0.8;
        return 1.0;
    }
}
