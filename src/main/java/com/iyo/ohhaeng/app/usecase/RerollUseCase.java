package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.user.User;
import com.iyo.ohhaeng.domain.weapon.ElementType;
import com.iyo.ohhaeng.domain.weapon.Weapon;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.db.WeaponRepository;
import com.iyo.ohhaeng.infra.time.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Component
public class RerollUseCase {

    private static final int MAX_REROLLABLE_LEVEL = 5;

    private final UserRepository userRepository;
    private final WeaponRepository weaponRepository;
    private final ClockHolder clockHolder;

    @Transactional
    public String execute(String userId) {
        Instant now = clockHolder.now();

        User user = userRepository.findByIdForUpdate(userId);
        Weapon weapon = weaponRepository.findByUserId(userId);

        if (user.isDown(now))                    return "기절 중입니다.";
        if (user.isRerollOnCooldown(now))         return "리롤 쿨다운 중입니다. (10분)";
        if (user.isRerollLimitReached(now))       return "오늘 리롤 횟수를 모두 사용했습니다. (일 3회)";
        if (weapon.getEnhanceLevel() > MAX_REROLLABLE_LEVEL)
                                                  return "강화 +" + MAX_REROLLABLE_LEVEL + " 이하만 리롤할 수 있습니다.";

        user.recalcResources(now);

        int cost = user.rerollCost();
        if (!user.hasGold(cost)) return "골드가 부족합니다. (필요: " + cost + "g)";

        ElementType before = weapon.getElementType();
        ElementType after  = pickOther(before);

        user.spendGold(cost);
        weapon.changeElement(after);
        weapon.rerollPenalty();
        user.applyReroll(now);

        userRepository.update(user);
        weaponRepository.update(weapon);

        return "[리롤 결과]\n"
                + before.name() + " → " + after.name()
                + "\n강화: +" + weapon.getEnhanceLevel()
                + "  골드 -" + cost;
    }

    private ElementType pickOther(ElementType current) {
        List<ElementType> others = Arrays.stream(ElementType.values())
                .filter(e -> e != current)
                .toList();
        return others.get(ThreadLocalRandom.current().nextInt(others.size()));
    }
}
