package com.iyo.ohhaeng.domain.user;

import com.iyo.ohhaeng.domain.weapon.ElementType;
import lombok.Getter;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static java.lang.Math.max;

@Getter
public class User {
    private String userId;
    private String userName;
    private int hp;
    private int maxHp;
    private int stamina;
    private int maxStamina;
    private long experience;
    private long gold;
    private Instant lastRerollAt;
    private int dailyRerollCount;
    private Instant lastCalcAt;
    private Instant downUntil;

    public User(String userId, int hp, int maxHp, int stamina, int maxStamina,
                long experience, long gold, Instant lastCalcAt, Instant downUntil, Instant lastRerollAt, int dailyRerollCount) {
        this.userId = userId;
        // userName is set via setUserName() by MyBatis result mapping
        this.hp = hp;
        this.maxHp = maxHp;
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.experience = experience;
        this.gold = gold;
        this.lastCalcAt = lastCalcAt;
        this.downUntil = downUntil;
        this.lastRerollAt = lastRerollAt;
        this.dailyRerollCount = dailyRerollCount;
    }

    public void setUserName(String userName) { this.userName = userName; }

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

    public static final int HUNT_EXP_REWARD  = 10;
    public static final int HUNT_GOLD_REWARD = 200;

    public void applyHuntResult(int damage, Instant now) {
        this.hp = max(0, hp - damage);
        this.experience += HUNT_EXP_REWARD;
        this.gold += HUNT_GOLD_REWARD;
        if (this.hp == 0) knockDown(now);
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

    public void applyDuelLoss(Instant now) {
        this.hp = Math.max(0, hp - 10);
        if (this.hp == 0) knockDown(now);
    }

    public void spendGold(int enhanceCost) {
        this.gold -= enhanceCost;
    }

    public boolean hasGold(int enhanceCost) {
        return this.gold >= enhanceCost;
    }

    public boolean isRerollOnCooldown(Instant now) {
        if (lastRerollAt == null) return false;
        return Duration.between(lastRerollAt, now).toMinutes() < 10;
    }

    public boolean isRerollLimitReached(Instant now) {
        if (lastRerollAt == null) return false;
        boolean sameDay = lastRerollAt.atZone(ZoneOffset.UTC).toLocalDate()
                .equals(now.atZone(ZoneOffset.UTC).toLocalDate());
        return sameDay && dailyRerollCount >= 3;
    }

    public int rerollCost() {
        return switch (dailyRerollCount) {
            case 0 -> 100;
            case 1 -> 200;
            default -> 400;
        };
    }

    public void applyReroll(Instant now) {
        boolean sameDay = lastRerollAt != null &&
                lastRerollAt.atZone(ZoneOffset.UTC).toLocalDate()
                        .equals(now.atZone(ZoneOffset.UTC).toLocalDate());
        if (!sameDay) dailyRerollCount = 0;
        dailyRerollCount++;
        lastRerollAt = now;
    }
}
