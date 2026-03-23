package com.iyo.ohhaeng.infra.db;

import com.iyo.ohhaeng.domain.ranking.RankEntry;

import java.util.List;

public interface RankingRepository {
    List<RankEntry> findTop(int limit);
}
