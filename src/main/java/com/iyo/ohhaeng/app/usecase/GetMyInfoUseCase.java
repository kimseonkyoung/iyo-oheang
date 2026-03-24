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

@RequiredArgsConstructor
@Component
public class GetMyInfoUseCase {

    private final UserRepository userRepository;
    private final WeaponRepository weaponRepository;
    private final ClockHolder clockHolder;

    @Transactional(readOnly = true)
    public String execute(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + userId));
        user.recalcResources(clockHolder.now());
        Weapon weapon = weaponRepository.findByUserId(userId);

        ElementType element = weapon.getElementType();
        return "[내 정보]\n"
                + "👤 이름: " + user.getUserName() + "\n"
                + "⚔️ 무기: " + weapon.displayName() + "\n"
                + element.emoji() + " 속성: " + element.display() + "\n"
                + "❤️ HP: " + user.getHp() + "/" + user.getMaxHp() + "\n"
                + "⚡ 스태미나: " + user.getStamina() + "/" + user.getMaxStamina() + "\n"
                + "💰 골드: " + user.getGold() + "\n"
                + "✨ 경험치: " + user.getExperience();
    }
}
