package com.iyo.ohhaeng.domain.weapon;

import java.util.Map;

public enum ElementType {
    WOOD, FIRE, EARTH, METAL, WATER;

    private static final Map<ElementType, ElementType> BEATS = Map.of(
            WOOD, EARTH,
            EARTH, WATER,
            WATER, FIRE,
            FIRE, METAL,
            METAL, WOOD
    );

    public boolean beats(ElementType other) {
        return BEATS.get(this) == other;
    }
}
