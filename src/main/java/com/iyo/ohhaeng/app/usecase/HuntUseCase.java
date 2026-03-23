package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.user.User;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.time.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Component
public class HuntUseCase {

    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    @Transactional
    public String execute(String userId) {
        Instant now = clockHolder.now();

        User user = userRepository.findByIdForUpdate(userId);

        if (user.isDown(now)) {
            return "기절 중입니다. 잠시 후 다시 시도해 주세요.";
        }

        user.recalcResources(now);

        if (user.getStamina() < 1) {
            return "스태미나가 부족합니다.";
        }

        int damage = ThreadLocalRandom.current().nextInt(10, 41);
        user.consumeStamina();
        user.applyHuntResult(damage, now);

        userRepository.update(user);

        return "[사냥 결과]\n"
                + "피해: " + damage
                + "  HP: " + user.getHp() + "/" + user.getMaxHp()
                + "\n경험치 +" + User.HUNT_EXP_REWARD + "  골드 +" + User.HUNT_GOLD_REWARD;
    }
}
