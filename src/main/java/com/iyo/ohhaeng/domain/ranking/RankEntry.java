package com.iyo.ohhaeng.domain.ranking;

import com.iyo.ohhaeng.domain.weapon.ElementType;
import lombok.Getter;

@Getter
public class RankEntry {

    private final String userName;
    private final ElementType elementType;
    private final int enhanceLevel;
    private final long experience;

    public RankEntry(String userName, ElementType elementType, int enhanceLevel, long experience) {
        this.userName = userName;
        this.elementType = elementType;
        this.enhanceLevel = enhanceLevel;
        this.experience = experience;
    }
}
