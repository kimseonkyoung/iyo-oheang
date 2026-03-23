package com.iyo.ohhaeng.infra.db.mapper;

import com.iyo.ohhaeng.domain.ranking.RankEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankingMapper {
    List<RankEntry> findTop(@Param("limit") int limit);
}
