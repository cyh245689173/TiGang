package com.copico.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.copico.mapper.ITrainingRecordMapper;
import com.copico.model.domain.TrainingRecord;
import com.copico.model.domain.User;
import com.copico.model.enums.UserRankEnum;
import com.copico.model.request.TrainingRecordRequest;
import com.copico.model.vo.TrainingStatsVO;
import com.copico.service.ITrainingRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final int EXP_PER_MINUTE = 100; // 每分钟训练获得的经验值

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void recordTraining(Long userId, TrainingRecordRequest request) {
        TrainingRecord record = new TrainingRecord();
        BeanUtils.copyProperties(request, record);
        record.setUserId(userId);
        record.setCalorie(BASE_CALORIE * record.getDuration());
        record.setTrainingDate(LocalDate.now());
        // 保存训练记录
        this.baseMapper.insert(record);

        // 增加用户经验值
        User user = userService.getById(userId);
        if (user != null) {
            // 如果没有经验值，默认设为 0
            long currentExp = user.getExp() == null ? 0 : user.getExp();
            long newExp = currentExp + (long) request.getDuration() * EXP_PER_MINUTE;
            user.setExp(newExp);

            // 如果没有等级，默认给最低等级
            UserRankEnum newRank = user.getUserLevel() == null ?
                    UserRankEnum.values()[0] : UserRankEnum.getRankByExp((int) newExp);
            user.setUserLevel(newRank.getDesc());

            // 更新用户信息
            userService.updateById(user);
        }
    }

    // 获取训练统计信息
    public TrainingStatsVO getTrainingStatistics(Long userId) {
        LocalDate today = LocalDate.now();
        TrainingStatsVO stats = new TrainingStatsVO();

        // 1. 总训练次数
        stats.setTotalTimes(this.baseMapper.countTotalTrainings(userId));

        // 2. 今日训练次数
        stats.setTodayTimes(this.baseMapper.countTodayTrainings(userId, today));

        // 3. 连续训练天数
        stats.setContinuousDays(calculateContinuousDays(userId, today));

        return stats;
    }

    /**
     * 计算连续训练天数（核心逻辑）
     *
     * @param userId
     * @param today
     * @return
     */
    private int calculateContinuousDays(Long userId, LocalDate today) {
        // 获取所有去重训练日期（按日期倒序）
        List<LocalDate> dates = this.baseMapper.selectDistinctTrainingDates(userId);
        if (dates.isEmpty()) return 0;

        // 转换为Set提高查询效率
        Set<LocalDate> dateSet = new HashSet<>(dates);

        int continuousDays = 0;
        LocalDate currentDay = today;

        // 从今天开始向前检查连续日期
        while (dateSet.contains(currentDay)) {
            continuousDays++;
            currentDay = currentDay.minusDays(1); // 向前推一天
        }

        return continuousDays;
    }

}
