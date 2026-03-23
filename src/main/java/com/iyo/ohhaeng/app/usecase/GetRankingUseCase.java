package com.iyo.ohhaeng.app.usecase;

import com.iyo.ohhaeng.domain.ranking.RankEntry;
import com.iyo.ohhaeng.infra.db.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class GetRankingUseCase {

    private static final int TOP_N = 20;

    private final RankingRepository rankingRepository;

    @Transactional(readOnly = true)
    public String execute() {
        List<RankEntry> entries = rankingRepository.findTop(TOP_N);

        if (entries.isEmpty()) {
            return "[랭킹] 아직 등록된 유저가 없습니다.";
        }

        StringBuilder sb = new StringBuilder("[랭킹] 서버 TOP ").append(TOP_N).append("\n\n");
        for (int i = 0; i < entries.size(); i++) {
            RankEntry e = entries.get(i);
            sb.append(String.format(Locale.KOREA, "%d위 %s %s +%d %,d EXP\n",
                    i + 1,
                    e.getUserName(),
                    e.getElementType().display(),
                    e.getEnhanceLevel(),
                    e.getExperience()));
        }
        return sb.toString().stripTrailing();
    }
}
