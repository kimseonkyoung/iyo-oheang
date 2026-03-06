package com.iyo.ohhaeng.domain.user;

import lombok.Getter;
import java.time.Duration;
import java.time.Instant;

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

}
