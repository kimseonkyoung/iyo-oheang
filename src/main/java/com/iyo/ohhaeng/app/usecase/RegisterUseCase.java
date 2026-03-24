package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.weapon.ElementType;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.db.WeaponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@RequiredArgsConstructor
@Component
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final WeaponRepository weaponRepository;

    private static final ElementType[] ELEMENTS = ElementType.values();
    private static final Random RANDOM = new Random();

    @Transactional
    public void ensureRegistered(String userId, String kakaoNickname) {
        if (userRepository.findById(userId).isPresent()) {
            return;
        }
        String userName = resolveUserName(userId, kakaoNickname);
        ElementType element = ELEMENTS[RANDOM.nextInt(ELEMENTS.length)];
        userRepository.insert(userId, userName);
        weaponRepository.insert(userId, element);
    }

    private String resolveUserName(String userId, String kakaoNickname) {
        if (kakaoNickname != null && !kakaoNickname.isBlank()) {
            return kakaoNickname;
        }
        // 닉네임 없으면 "용사-NNNN" 형태로 자동 생성
        return "용사-" + (Math.abs(userId.hashCode()) % 9000 + 1000);
    }
}
