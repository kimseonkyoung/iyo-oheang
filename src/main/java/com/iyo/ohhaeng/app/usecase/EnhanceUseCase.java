package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.user.User;
import com.iyo.ohhaeng.domain.weapon.EnhanceCalculator;
import com.iyo.ohhaeng.domain.weapon.Weapon;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.db.WeaponRepository;
import com.iyo.ohhaeng.infra.time.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class EnhanceUseCase {

    private static final int ENHANCE_COST = 500;

    private final UserRepository userRepository;
    private final WeaponRepository weaponRepository;
    private final EnhanceCalculator enhanceCalculator;
    private final ClockHolder clockHolder;

    @Transactional
    public String execute(String userId) {
        Instant now = clockHolder.now();

        User user = userRepository.findByIdForUpdate(userId);
        Weapon weapon = weaponRepository.findByUserId(userId);

        if (user.isDown(now)) return "기절 중입니다.";
        if (weapon.isMaxLevel()) return "이미 최대 강화 레벨입니다.";
        user.recalcResources(now);
        if (!user.hasGold(ENHANCE_COST)) return "골드가 부족합니다.";
        user.spendGold(ENHANCE_COST);

        String result = switch (enhanceCalculator.roll(weapon.getEnhanceLevel())) {
            case SUCCESS   -> { weapon.enhance();  yield "강화 성공! +"  + weapon.getEnhanceLevel(); }
            case HOLD      ->                             "강화 실패. +"  + weapon.getEnhanceLevel();
            case DOWNGRADE -> { weapon.degrade(); yield "강화 하락... +" + weapon.getEnhanceLevel(); }
        };

        userRepository.update(user);
        weaponRepository.update(weapon);

        return "[강화 결과]\n" + result + "\n골드 -" + ENHANCE_COST;
    }
}
