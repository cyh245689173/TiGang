package com.copico.service.impl;

import com.copico.mapper.ITrainingRecordMapper;
import com.copico.model.vo.RankingItemVO;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;

@Service
public class RankingService {

    private final ITrainingRecordMapper trainingRecordMapper;

    public RankingService(ITrainingRecordMapper trainingRecordMapper) {
        this.trainingRecordMapper = trainingRecordMapper;
    }

    // 获取总时长排行榜
    public List<RankingItemVO> getTotalRanking() {
        return addRankingPosition(trainingRecordMapper.selectTotalRanking());
    }

    // 获取周排行榜
    public List<RankingItemVO> getWeeklyRanking() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        return addRankingPosition(
            trainingRecordMapper.selectWeeklyRanking(startOfWeek, endOfWeek)
        );
    }

    // 获取日排行榜
    public List<RankingItemVO> getDailyRanking() {
        return addRankingPosition(
            trainingRecordMapper.selectDailyRanking(LocalDate.now())
        );
    }

    // 为排行榜添加排名位置
    private List<RankingItemVO> addRankingPosition(List<RankingItemVO> list) {
        if (list.isEmpty()) return Collections.emptyList();
        
        int rank = 1;
        int lastDuration = list.get(0).getTotalDuration();
        
        for (int i = 0; i < list.size(); i++) {
            RankingItemVO item = list.get(i);
            
            // 处理并列排名
            if (item.getTotalDuration() < lastDuration) {
                rank = i + 1;
                lastDuration = item.getTotalDuration();
            }
            
            item.setRank(rank);
        }
        return list;
    }
}