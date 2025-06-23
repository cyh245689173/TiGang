package com.copico.model.vo;

import lombok.Data;

/**
 * 排行榜VO对象
 */
@Data
public class RankingItemVO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer totalDuration; // 总时长（分钟）
    private Integer totalCalorie; // 总消耗熱量
    private Integer rank;         // 排名
    private String userLevel;      // 添加用户等级字段
    private Integer exp;      // 添加用户经验值
}