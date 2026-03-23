package com.iyo.ohhaeng.domain.weapon;

import lombok.Getter;

@Getter
public class Weapon {

    private static final int MIN_ENHANCE_LEVEL = 0;
    private static final int MAX_ENHANCE_LEVEL = 30;
    private static final int TIER_SIZE         = 6;

    private String weaponId;
    private String userId;
    private ElementType elementType;
    private int enhanceLevel;

    public Weapon(String weaponId, String userId, ElementType elementType, int enhanceLevel) {
        this.weaponId = weaponId;
        this.userId = userId;
        this.elementType = elementType;
        this.enhanceLevel = enhanceLevel;
    }

    /**
     * 무기 표시명 — 티어별 이름 + 별(★) + 강화 수치
     * 티어: enhanceLevel / 6  (0~4=일반, 5=전설)
     * 별:   enhanceLevel % 6  (0~5개)  단, +30(전설)은 ★★★★★ 고정
     */
    public String displayName() {
        if (enhanceLevel == MAX_ENHANCE_LEVEL) {
            return elementType.weaponName(5) + " ★★★★★ (+" + enhanceLevel + ")";
        }
        int tier    = enhanceLevel / TIER_SIZE;
        int stars   = enhanceLevel % TIER_SIZE;
        String name = elementType.weaponName(tier);
        String bar  = "★".repeat(stars) + "☆".repeat(5 - stars);
        return name + " " + bar + " (+" + enhanceLevel + ")";
    }

    public void rerollPenalty() {
        enhanceLevel = Math.max(MIN_ENHANCE_LEVEL, enhanceLevel - 2);
    }

    public void enhance() {
        this.enhanceLevel++;
    }

    public void degrade() {
        this.enhanceLevel--;
    }

    public boolean isMaxLevel() {
        return enhanceLevel >= 30;
    }

    public void changeElement(ElementType newElement) {
        this.elementType = newElement;
    }
}
