package com.iyo.ohhaeng.infra.db.mapper;

import com.iyo.ohhaeng.domain.battle.Battle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BattleMapper {
    void save(Battle battle);
}
