package com.copico.model.vo;

import lombok.Data;

@Data
public class TrainingStatsVO {

    /**
     * 总训练次数
     */
    private Integer totalTimes;
    /**
     * 今日训练次数
     */

    private Integer todayTimes;

    /**
     * 连续训练天数
     */
    private Integer continuousDays;
}