package com.iyo.ohhaeng.app.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyo.ohhaeng.domain.battle.Battle;
import com.iyo.ohhaeng.domain.battle.BattleCalculator;
import com.iyo.ohhaeng.domain.battle.BattleResult;
import com.iyo.ohhaeng.domain.weapon.Weapon;
import com.iyo.ohhaeng.domain.user.User;
import com.iyo.ohhaeng.infra.db.BattleRepository;
import com.iyo.ohhaeng.infra.db.UserRepository;
import com.iyo.ohhaeng.infra.db.WeaponRepository;
import com.iyo.ohhaeng.infra.time.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DuelUseCase {

    private final UserRepository userRepository;
    private final WeaponRepository weaponRepository;
    private final BattleRepository battleRepository;
    private final BattleCalculator battleCalculator;
    private final ClockHolder clockHolder;
    private final ObjectMapper objectMapper;

    @Transactional
    public String execute(String userId, String targetName) {
        Instant now = clockHolder.now();

        // 1. 락 없이 상대 userId 조회
        String targetId = userRepository.findByName(targetName)
                .orElseThrow(() -> new IllegalArgumentException("상대를 찾을 수 없습니다: " + targetName))
                .getUserId();

        // 2. 데드락 방지: userId 사전순으로 락 순서 고정 후 읽기
        boolean attackerFirst = userId.compareTo(targetId) < 0;
        User first  = userRepository.findByIdForUpdate(attackerFirst ? userId   : targetId);
        User second = userRepository.findByIdForUpdate(attackerFirst ? targetId : userId);

        User attacker = attackerFirst ? first : second;
        User target   = attackerFirst ? second : first;

        if (attacker.isDown(now)) return "기절 중입니다. 잠시 후 다시 시도해 주세요.";
        if (target.isDown(now))   return "상대가 기절 중입니다.";

        attacker.recalcResources(now);
        target.recalcResources(now);

        Weapon aWeapon = weaponRepository.findByUserId(userId);
        Weapon bWeapon = weaponRepository.findByUserId(target.getUserId());

        BattleResult result = battleCalculator.calculate(aWeapon, bWeapon);

        switch (result.getOutcome()) {
            case "WIN_A" -> target.applyDuelLoss(now);
            case "WIN_B" -> attacker.applyDuelLoss(now);
        }

        String roundLogJson = buildRoundLog(userId, target.getUserId(), aWeapon, bWeapon, result, now);
        Battle battle = Battle.of("GLOBAL_1", userId, target.getUserId(),
                result.getASum(), result.getBSum(), result.getOutcome(), roundLogJson, now);
        battleRepository.save(battle);

        userRepository.update(attacker);
        userRepository.update(target);

        String resultText = switch (result.getOutcome()) {
            case "WIN_A" -> "🏆 승리! (" + result.getASum() + " vs " + result.getBSum() + ")";
            case "WIN_B" -> "💀 패배... (" + result.getASum() + " vs " + result.getBSum() + ")";
            default      -> "🤝 무승부! (" + result.getASum() + " vs " + result.getBSum() + ")";
        };
        return "[⚔️ 대결 결과]\nvs @" + targetName + "\n" + resultText;
    }

    private String buildRoundLog(String aUserId, String bUserId,
                                 Weapon aWeapon, Weapon bWeapon,
                                 BattleResult result, Instant createdAt) {
        Map<String, Object> log = Map.of(
                "version", "BATTLE_LOG_V0",
                "mode", "PVP",
                "createdAt", createdAt.toString(),
                "players", Map.of(
                        "a", Map.of("userId", aUserId,
                                "element", aWeapon.getElementType().name(),
                                "enh", aWeapon.getEnhanceLevel()),
                        "b", Map.of("userId", bUserId,
                                "element", bWeapon.getElementType().name(),
                                "enh", bWeapon.getEnhanceLevel())
                ),
                "roundsDetail", result.getRounds(),
                "result", Map.of("sumA", result.getASum(), "sumB", result.getBSum(),
                        "outcome", result.getOutcome())
        );
        try {
            return objectMapper.writeValueAsString(log);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
