package com.iyo.ohhaeng.domain.weapon;

import lombok.Getter;

@Getter
public class Weapon {

    private static final int MIN_ENHANCE_LEVEL = 0;

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
}
