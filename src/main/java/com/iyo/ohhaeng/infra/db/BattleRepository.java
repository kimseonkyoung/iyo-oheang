package com.iyo.ohhaeng.infra.db;

import com.iyo.ohhaeng.domain.battle.Battle;

public interface BattleRepository {
    void save(Battle battle);
}
