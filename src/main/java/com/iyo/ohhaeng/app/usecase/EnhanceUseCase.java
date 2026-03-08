package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.user.User;
import com.iyo.ohhaeng.domain.weapon.Weapon;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.db.WeaponRepository;
import com.iyo.ohhaeng.infra.time.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Component
public class EnhanceUseCase {

    private static final int ENHANCE_COST = 500;

    /** [성공%, 유지%, 하락%] — 행 합계 = 100, 인덱스 = 현재 강화 레벨 */
    private static final int[][] ENHANCE_RATES = {
        {90, 10,  0},  // L0
        {85, 12,  3},  // L1
        {80, 15,  5},  // L2
        {75, 17,  8},  // L3
        {70, 18, 12},  // L4
        {65, 20, 15},  // L5
        {55, 22, 23},  // L6
        {45, 25, 30},  // L7
        {35, 28, 37},  // L8
        {25, 30, 45},  // L9
        {22, 30, 48},  // L10
        {20, 28, 52},  // L11
        {18, 27, 55},  // L12
        {15, 25, 60},  // L13
        {12, 23, 65},  // L14
        {10, 20, 70},  // L15
        { 9, 18, 73},  // L16
        { 8, 17, 75},  // L17
        { 7, 15, 78},  // L18
        { 6, 14, 80},  // L19
        { 5, 13, 82},  // L20
        { 5, 12, 83},  // L21
        { 4, 11, 85},  // L22
        { 4, 10, 86},  // L23
        { 3, 10, 87},  // L24
        { 3,  7, 90},  // L25 ← 극악 구간
        { 2,  5, 93},  // L26
        { 1,  4, 95},  // L27
        { 1,  2, 97},  // L28
        { 1,  1, 98},  // L29
    };

    private final UserRepository userRepository;
    private final WeaponRepository weaponRepository;
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

        String result = switch (rollOutcome(rates(weapon.getEnhanceLevel()))) {
            case SUCCESS   -> { weapon.enhance();  yield "강화 성공! +"  + weapon.getEnhanceLevel(); }
            case HOLD      ->                             "강화 실패. +"  + weapon.getEnhanceLevel();
            case DOWNGRADE -> { weapon.degrade(); yield "강화 하락... +" + weapon.getEnhanceLevel(); }
        };

        userRepository.update(user);
        weaponRepository.update(weapon);

        return "[강화 결과]\n" + result + "\n골드 -" + ENHANCE_COST;
    }

    private Outcome rollOutcome(int[] r) {
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll < r[0])        return Outcome.SUCCESS;
        if (roll < r[0] + r[1]) return Outcome.HOLD;
        return Outcome.DOWNGRADE;
    }

    /** enhanceLevel 기준 [성공%, 유지%, 하락%] 반환 */
    private int[] rates(int level) {
        return ENHANCE_RATES[level];
    }

    private enum Outcome { SUCCESS, HOLD, DOWNGRADE }
}
