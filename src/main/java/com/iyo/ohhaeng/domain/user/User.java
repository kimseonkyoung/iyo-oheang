package com.iyo.ohhaeng.domain.user;

import lombok.Getter;
import java.time.Duration;
import java.time.Instant;

import static java.lang.Math.max;

@Getter
public class User {
    private String userId;
    private int hp;
    private int maxHp;
    private int stamina;
    private int maxStamina;
    private long experience;
    private long gold;
    private Instant lastCalcAt;
    private Instant downUntil;

    public User(String userId, int hp, int maxHp, int stamina, int maxStamina,
                long experience, long gold, Instant lastCalcAt, Instant downUntil) {
        this.userId = userId;
        this.hp = hp;
        this.maxHp = maxHp;
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.experience = experience;
        this.gold = gold;
        this.lastCalcAt = lastCalcAt;
        this.downUntil = downUntil;
    }

    public void recalcResources(Instant now) {
        long ticks = Duration.between(lastCalcAt, now).toSeconds() / 10;
        hp = Math.min(maxHp, hp + (int) ticks);
        stamina = Math.min(maxStamina, stamina + (int) ticks);
        lastCalcAt = lastCalcAt.plusSeconds(ticks * 10);
    }

    public void knockDown(Instant now) {
        this.downUntil = now.plusSeconds(60 * 10);  // 10분 = 600초

    }

    public void consumeStamina() {
        this.stamina -= 1;
    }

    public void applyHuntResult(int damage) {
        this.hp = max(0, hp - damage);
        this.experience += 10;
        this.gold += 200;
    }

    public boolean isDown(Instant now) {
        if (downUntil == null) {
            return false;
        }
        if (now.isBefore(downUntil)) {
            return true;
        }
        downUntil = null;
        return false;
    }
}
