package com.copico.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.copico.mapper.ITrainingRecordMapper;
import com.copico.mapper.UserDailySummaryMapper;
import com.copico.model.domain.TrainingRecord;
import com.copico.model.domain.UserDailySummary;
import com.copico.model.request.TrainingRecordRequest;
import com.copico.service.ITrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 训练记录 服务实现类
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-16
 */
@Service
public class TrainingRecordServiceImpl extends ServiceImpl<ITrainingRecordMapper, TrainingRecord> implements ITrainingRecordService {

    private static final int BASE_CALORIE = 5; // 基础卡路里消耗（每分钟）

    @Autowired
    UserDailySummaryMapper dailySummaryMapper;

    @Override
    public TrainingRecord recordTraining(Long userId, TrainingRecordRequest request) {
        TrainingRecord record = new TrainingRecord();
        BeanUtil.copyProperties(request, record);
        // 保存训练记录
        this.baseMapper.insert(record);

        // 更新每日排行榜
        updateDailySummary(userId, record.getDuration(), calculateCalories(record.getDifficulty(), record.getDuration()));
        return null;
    }


    /**
     * 卡路里计算
     *
     * @param difficulty
     * @param duration
     * @return
     */
    public double calculateCalories(Integer difficulty, int duration) {
        //TODO:后续采用比较科学一点的计算方法，根据 难度 持续时间 共同决定消耗的卡路里
        double level = switch (difficulty) {
            case 0 -> 1;
            case 1 -> 1.5;
            case 2 -> 2;
            default -> 0;
        };
        return level * BASE_CALORIE * duration;
    }

    @Override
    public Object getDailyRanking() {
        return null;
    }

    /**
     * 更新每日训练总量
     *
     * @param userId
     * @param duration
     * @param calories
     */
    private void updateDailySummary(Long userId, int duration, double calories) {
        Date today = new Date();
        UserDailySummary summary = dailySummaryMapper.selectByUserAndDate(userId, today);
        //没有则新建
        if (summary == null) {
            summary = new UserDailySummary();
            summary.setUserId(userId);
            summary.setSummaryDate(today);
            summary.setTotalDuration(duration);
            summary.setTotalCalories(calories);
            dailySummaryMapper.insert(summary);
        } else {
            //有则更新总时长和总卡路里
            summary.setTotalDuration(summary.getTotalDuration() + duration);
            summary.setTotalCalories(summary.getTotalCalories() + calories);
            dailySummaryMapper.updateById(summary);
        }
    }


}
