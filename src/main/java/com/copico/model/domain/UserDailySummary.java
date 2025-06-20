package com.copico.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.copico.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@TableName("user_daily_summary")
@EqualsAndHashCode(callSuper = true)
public class UserDailySummary extends BaseEntity<UserDailySummary> {
    
    private Long userId;
    
    private Date summaryDate;
    
    private Integer totalDuration;
    
    private Double totalCalories;
}