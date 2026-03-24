package com.iyo.ohhaeng.domain.weapon;

import java.util.Map;

public enum ElementType {

    WOOD ("목(木)", "🌿", new String[]{"나뭇가지",  "목검(木劍)",   "고목검(古木劍)", "정령검(精靈劍)", "청룡검(靑龍劍)", "세계수(世界樹)"}),
    FIRE ("화(火)", "🔥", new String[]{"부싯돌",    "화검(火劍)",   "염화검(炎火劍)", "화룡검(火龍劍)", "주작검(朱雀劍)", "태양의 핵(太陽核)"}),
    EARTH("토(土)", "⛰️", new String[]{"흙덩이",    "석검(石劍)",   "암석검(巖石劍)", "대지검(大地劍)", "황제검(黃帝劍)", "대지의 심장(大地心)"}),
    METAL("금(金)", "⚙️", new String[]{"쇳조각",    "철검(鐵劍)",   "강철검(鋼鐵劍)", "금강검(金剛劍)", "백호검(白虎劍)", "하늘의 날(天刃)"}),
    WATER("수(水)", "💧", new String[]{"물방울",    "수검(水劍)",   "파도검(波濤劍)", "심해검(深海劍)", "현무검(玄武劍)", "용궁의 파도(龍宮波)"});

    private final String display;
    private final String emoji;
    private final String[] weaponNames;

    ElementType(String display, String emoji, String[] weaponNames) {
        this.display     = display;
        this.emoji       = emoji;
        this.weaponNames = weaponNames;
    }

    public String display() { return display; }
    public String emoji()   { return emoji; }

    /** tier 0~4 = 일반, tier 5 = 전설 */
    public String weaponName(int tier) {
        return weaponNames[Math.min(tier, 5)];
    }

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
