package com.copico.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.copico.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 训练记录
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("training_record")
public class TrainingRecord extends BaseEntity<TrainingRecord> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 训练难度
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 训练难度
     */
    @TableField("difficulty")
    private Integer difficulty;

    /**
     * 训练时长, 单位: min
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 训练卡路里, 单位: kcal
     */
    @TableField("calorie")
    private Integer calorie;


    /**
     * 训练日期
     */
    @TableField("training_date")
    private LocalDate trainingDate;

}
