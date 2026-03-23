package com.iyo.ohhaeng.infra.db.oracle;

import com.iyo.ohhaeng.domain.ranking.RankEntry;
import com.iyo.ohhaeng.infra.db.RankingRepository;
import com.iyo.ohhaeng.infra.db.mapper.RankingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OracleRankingRepository implements RankingRepository {

    private final RankingMapper rankingMapper;

    @Override
    public List<RankEntry> findTop(int limit) {
        return rankingMapper.findTop(limit);
    }
}
