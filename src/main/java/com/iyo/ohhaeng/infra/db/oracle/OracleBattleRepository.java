package com.iyo.ohhaeng.infra.db.oracle;

import com.iyo.ohhaeng.domain.battle.Battle;
import com.iyo.ohhaeng.infra.db.BattleRepository;
import com.iyo.ohhaeng.infra.db.mapper.BattleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OracleBattleRepository implements BattleRepository {

    private final BattleMapper battleMapper;

    @Override
    public void save(Battle battle) {
        battleMapper.save(battle);
    }
}
