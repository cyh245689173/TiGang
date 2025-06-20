package com.copico.model.request;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @author 陈玉皓
 * @date 2025/6/16 20:56
 * @description: TODO
 */
public class TrainingRecordRequest {

    /**
     * 训练难度
     */
    @TableField("difficulty")
    private String difficulty;

    /**
     * 训练时长, 单位: min
     */
    @TableField("duration")
    private Integer duration;

}
