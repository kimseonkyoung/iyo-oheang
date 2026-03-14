package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.user.User;
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
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.recalcResources(clockHolder.now());
        Weapon weapon = weaponRepository.findByUserId(userId);

        return "[내 정보]\n속성: " + weapon.getElementType() + "  강화: +" + weapon.getEnhanceLevel()
                + "\nHP: " + user.getHp() + "/" + user.getMaxHp()
                + "  스태미나: " + user.getStamina() + "/" + user.getMaxStamina();
    }
}
